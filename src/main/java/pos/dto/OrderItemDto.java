package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.InventoryUpdateForm;
import pos.model.OrderItemData;
import pos.model.OrderItemForm;
import pos.model.OrderItemUpdateForm;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.services.InventoryServices;
import pos.services.OrderItemServices;
import pos.services.OrderServices;
import pos.services.ProductServices;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static pos.util.DataUtil.validate;
import static pos.util.HelperUtil.convertOrderItemFormToPojo;
import static pos.util.HelperUtil.convertPojoToOrderData;

@Service
public class OrderItemDto {

    @Autowired
    OrderItemServices orderItemServices;
    @Autowired
    OrderServices orderServices;
    @Autowired
    ProductServices productServices;
    @Autowired
    InventoryServices inventoryServices;

    public List<OrderItemData> getAll() throws ApiException{
        List<OrderItemPojo> p =  orderItemServices.getAll();
        List<OrderItemData> b = new ArrayList<>();
        for( OrderItemPojo pj : p){
            b.add(convertPojoToOrderData(pj));
        }
        return b;
    }

    public List<OrderItemData> getOrderItemForOrder(Integer orderId) throws ApiException{
        List<OrderItemPojo> orderItemPojoList = getOrderItemForOrderUtil(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(OrderItemPojo i : orderItemPojoList){
            OrderItemData j = convertPojoToOrderData(i);
            orderItemDataList.add(j);
        }
        return orderItemDataList;
    }

    public void add(OrderItemForm orderItemForm) throws ApiException{
        validateOrderItemForm(orderItemForm);
        Integer availableQty = getAvailQty(orderItemForm.getProductId());
        updateQtyInventory(orderItemForm,availableQty);
        orderItemServices.add(convertOrderItemFormToPojo(orderItemForm));
    }
    @Transactional(rollbackOn = ApiException.class)
    public void update(OrderItemUpdateForm orderItemUpdateForm) throws ApiException{
        validate(orderItemUpdateForm,"Body values cannot be null");
        checkOrderStatusByOrderItemId(orderItemUpdateForm.getId());
        updateInventoryQty(orderItemUpdateForm);
        orderItemServices.update(convertOrderItemFormToPojo(orderItemUpdateForm));
    }

    public void delete(Integer id) throws ApiException{

        updateDeleteInventory(id);
        orderItemServices.delete(id);
    }

    public OrderItemData get(Integer id) throws ApiException{
        return convertPojoToOrderData(orderItemServices.get(id));
    }

    @Transactional(rollbackOn = ApiException.class)
    private void validateOrderItemForm(OrderItemForm p) throws ApiException {
        validate(p,"body values cannot be null");

        if(orderItemServices.selectFromOrderIdProductId(p.getOrderId(),p.getProductId())!=null){
            throw new ApiException("OrderItem already exist update that instead");
        }
        OrderPojo orderPojo = orderServices.get(p.getOrderId());

        if (orderPojo == null) {
            throw new ApiException("Order with this id does not exist, id : " + p.getOrderId());
        }

        ProductPojo productPojo = productServices.get(p.getProductId());

        if (productPojo == null) {
            throw new ApiException("Product with this id does not exist, id : " + p.getProductId());
        }

        int availQty = getAvailQty(p.getProductId());

        if(p.getQuantity()>availQty){
            throw new ApiException("Selected quantity more than available quantity, available quantity only "
                    + availQty );
        }

        int orderId = p.getOrderId();
        if(checkOrderStatus(orderId)){
            throw new ApiException("Cannot add as order already placed for order id : " + p.getOrderId());
        }

    }

    private boolean checkOrderStatus(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderServices.get(orderId);
        return orderPojo.getOrderPlaced();
    }
    private Integer getAvailQty(Integer productId) throws ApiException {
        InventoryPojo inv = inventoryServices.selectByProductId(productId);
        if (inv == null) {
            throw new ApiException("Product with this id does not exist in the inventory, id : " + productId);
        }
        int invId = inv.getId();
        return inventoryServices.get(invId).getQuantity();
    }
    private void updateQtyInventory(OrderItemForm orderItemForm, Integer availQty) throws ApiException {
        InventoryUpdateForm inv = new InventoryUpdateForm();
        if(orderItemForm.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 0");
        }

        int invId = inventoryServices.selectByProductId(orderItemForm.getProductId()).getId();
        inv.setId(invId);
        inv.setQuantity(availQty-orderItemForm.getQuantity());
        inventoryServices.update(inv);
    }

    @Transactional(rollbackOn = ApiException.class)
    private List<OrderItemPojo> getOrderItemForOrderUtil(Integer orderId) throws ApiException {
        OrderPojo oPojo = orderServices.get(orderId);
        if (oPojo == null) {
            throw new ApiException("Order with this id does not exist, id : " + orderId);
        }
        return orderItemServices.selectFromOrderId(orderId);

    }
    private void checkOrderStatusByOrderItemId(Integer OrderItemId) throws ApiException {
        int orderId = orderItemServices.get(OrderItemId).getOrderId();
        if(checkOrderStatus(orderId)){
            throw new ApiException("Cannot update as order already placed for order id : " + orderId);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    private void updateInventoryQty(OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        if(orderItemUpdateForm.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 1");
        }
        OrderItemPojo orderItemExist = orderItemServices.get(orderItemUpdateForm.getId());

        InventoryPojo inv = inventoryServices.selectByProductId(orderItemExist.getProductId());
        if (inv == null){
            throw new ApiException("Product associated with given orderItem id does not exist in the inventory, product id : "
                    + orderItemExist.getProductId());
        }
        int invId = inv.getId();
        int availQty = inventoryServices.get(invId).getQuantity();
        if (availQty + orderItemExist.getQuantity() >= orderItemUpdateForm.getQuantity()){
            InventoryUpdateForm invUpdate = new InventoryUpdateForm();
            invUpdate.setId(invId);
            invUpdate.setQuantity(availQty + orderItemExist.getQuantity() - orderItemUpdateForm.getQuantity());
            inventoryServices.update(invUpdate);
        }
        else{
            throw new ApiException("Selected quantity more than available quantity, available quantity only "
                    + availQty );
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    private void updateDeleteInventory(Integer id) throws ApiException {
        int OrderId = orderItemServices.get(id).getOrderId();
        if(checkOrderStatus(OrderId)){
            throw new ApiException("Cannot delete as order already placed for id : " + id);
        }
        OrderItemPojo ex = orderItemServices.get(id);
        InventoryPojo inv = inventoryServices.selectByProductId(ex.getProductId());
        if(inv == null){
            throw new ApiException("Product with given product id does not exist in the inventory, product id : "
                    + ex.getProductId());
        }
        int invId = inv.getId();
        int availQty = inventoryServices.get(invId).getQuantity();
        InventoryUpdateForm invUpdate = new InventoryUpdateForm();
        invUpdate.setId(invId);
        invUpdate.setQuantity(availQty + ex.getQuantity());
        inventoryServices.update(invUpdate);
    }
}
