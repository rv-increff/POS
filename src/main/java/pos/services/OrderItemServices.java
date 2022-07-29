package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.InventoryDao;
import pos.dao.OrderDao;
import pos.dao.OrderItemDao;
import pos.dao.ProductDao;
import pos.dto.OrderDto;
import pos.model.InventoryUpdateForm;
import pos.model.OrderData;
import pos.model.OrderItemForm;
import pos.model.OrderItemUpdateForm;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.List;

import static pos.util.DataUtil.checkNotNullUtil;

@Service
public class OrderItemServices {

    @Autowired
    private OrderDao oDao;
    @Autowired
    private ProductDao pDao;
    @Autowired
    private OrderItemDao dao;

    @Autowired
    private InventoryDao iDao;

    @Autowired
    private InventoryServices invService;
    @Autowired
    private OrderServices OServices;
    @Autowired
    private OrderDto ODto;

    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderItemForm p) throws ApiException {

        checkNotNullUtil(p,"body values cannot be null");
        if(dao.selectFromOrderIdProductId(p.getOrderId(),p.getProductId())!=null){
            throw new ApiException("OrderItem already exist update that instead");
        }
        OrderPojo oPojo = oDao.select(p.getOrderId());

        if (oPojo == null) {
            throw new ApiException("Order with this id does not exist, id : " + p.getOrderId());
        }

        ProductPojo pPojo = pDao.select(p.getProductId());

        if (pPojo == null) {
            throw new ApiException("Product with this id does not exist, id : " + p.getProductId());
        }

        InventoryPojo inv = iDao.selectFromProductId(p.getProductId());
        if (inv == null) {
            throw new ApiException("Product with this id does not exist in the inventory, id : " + p.getProductId());
        }
        int invId = inv.getId();
        int availQty = iDao.select(invId).getQuantity();

        if(p.getQuantity()>availQty){
            throw new ApiException("Selected quantity more than available quantity, available quantity only "
                    + availQty );
        }

        int orderId = p.getOrderId();
        if(checkOrderStatus(orderId)){
            throw new ApiException("Cannot add as order already placed for order id : " + p.getOrderId());
        }
        if(p.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 1");
        }
        OrderItemPojo oiPojo = new OrderItemPojo();
        oiPojo.setOrderId(p.getOrderId());
        oiPojo.setProductId(p.getProductId());
        oiPojo.setSellingPrice(p.getSellingPrice());
        oiPojo.setQuantity(p.getQuantity());
        //remove from inventory
        updateQtyInventory(p,availQty);
        dao.add(oiPojo);

        }

    @Transactional(rollbackOn = ApiException.class)
    public void updateQtyInventory(OrderItemForm p, int availQty) throws ApiException {
        InventoryUpdateForm inv = new InventoryUpdateForm();
        if(p.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 1");
        }

        int invId = iDao.selectFromProductId(p.getProductId()).getId();
        inv.setId(invId);
        inv.setQuantity(availQty-p.getQuantity());
        invService.update(inv);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemPojo> getAll() throws ApiException {
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemPojo> getOrderItemForOrder(int orderId) throws ApiException {
        OrderPojo oPojo = oDao.select(orderId);
        if (oPojo == null) {
            throw new ApiException("Order with this id does not exist, id : " + orderId);
        }
        return dao.selectFromOrderId(orderId);

    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(OrderItemUpdateForm p) throws ApiException {
        checkNotNullUtil(p,"Body values cannot be null");
        getCheck(p.getId());
        if(p.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 1");
        }
        int orderId = dao.select(p.getId()).getOrderId();
        if(checkOrderStatus(orderId)){
            throw new ApiException("Cannot update as order already placed for order id : " + orderId);
        }
        updateUtil(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemPojo getCheck(int id) throws ApiException {
        OrderItemPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("OrderItem with given id does not exist ,id : " + id);
        }
        return p;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int id) throws ApiException {
        getCheck(id);
        int OrderId = dao.select(id).getOrderId();
        if(checkOrderStatus(OrderId)){
            throw new ApiException("Cannot delete as order already placed for id : " + id);
        }
        OrderItemPojo ex = dao.select(id);
        InventoryPojo inv = iDao.selectFromProductId(ex.getProductId());
        if(inv==null){
            throw new ApiException("Product with given product id does not exist in the inventory, product id : "
                    + ex.getProductId());
        }
        int invId = inv.getId();
        int availQty = iDao.select(invId).getQuantity();
        InventoryUpdateForm invUpdate = new InventoryUpdateForm();
        invUpdate.setId(invId);
        invUpdate.setQuantity(availQty + ex.getQuantity());
        invService.update(invUpdate);
        dao.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public boolean checkOrderItemWithProductId(int productId){
        return dao.selectFromProductId(productId)==null;
    }

    public boolean checkOrderStatus(int orderId) throws ApiException {
        OrderData p = ODto.get(orderId);
        return p.isOrderPlaced();
    }

    private void updateUtil(OrderItemUpdateForm p) throws ApiException {
        OrderItemPojo ex = dao.select(p.getId());

        InventoryPojo inv = iDao.selectFromProductId(ex.getProductId());
        if (inv == null){
            throw new ApiException("Product with given product id does not exist in the inventory, product id : "
                    + ex.getProductId());
        }
        int invId = inv.getId();
        int availQty = iDao.select(invId).getQuantity();
        if (availQty + ex.getQuantity() >= p.getQuantity()){
            InventoryUpdateForm invUpdate = new InventoryUpdateForm();
            invUpdate.setId(invId);
            invUpdate.setQuantity(availQty + ex.getQuantity() - p.getQuantity());
            invService.update(invUpdate);

            ex.setQuantity(p.getQuantity());
            ex.setSellingPrice(p.getSellingPrice());
            dao.update(); //symbolic
        }
        else{
            throw new ApiException("Selected quantity more than available quantity, available quantity only "
                    + availQty );
        }
    }



}
