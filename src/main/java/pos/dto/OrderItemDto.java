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

import static java.util.Objects.isNull;
import static pos.util.DataUtil.validate;
import static pos.util.HelperUtil.convertOrderItemFormToPojo;
import static pos.util.HelperUtil.convertPojoToOrderItemData;

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

    public List<OrderItemData> getAll() throws ApiException {
        List<OrderItemPojo> orderItemPojoList = orderItemServices.getAll();
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            orderItemDataList.add(convertPojoToOrderItemData(orderItemPojo));
        }

        return orderItemDataList;
    }

    public List<OrderItemData> getOrderItemForOrder(Integer orderId) throws ApiException {

        OrderPojo oPojo = orderServices.get(orderId);
        if (isNull(oPojo)) {
            throw new ApiException("Order with this id does not exist, id : " + orderId);
        }

        List<OrderItemPojo> orderItemPojoList = orderItemServices.selectFromOrderId(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo orderItemPojo : orderItemPojoList) {
            OrderItemData orderItemData = convertPojoToOrderItemData(orderItemPojo);
            orderItemDataList.add(orderItemData);
        }

        return orderItemDataList;
    }

    public OrderItemForm add(OrderItemForm orderItemForm) throws ApiException {

        validateOrderItemForm(orderItemForm);
        Integer availableQty = getAvailQty(orderItemForm.getProductId());

        InventoryUpdateForm inv = new InventoryUpdateForm();
        if (orderItemForm.getQuantity() <= 0) {
            throw new ApiException("Quantity must be greater than 0");
        }
        int invId = inventoryServices.selectByProductId(orderItemForm.getProductId()).getId();
        inv.setId(invId);
        inv.setQuantity(availableQty - orderItemForm.getQuantity());
        inventoryServices.update(inv);

        orderItemServices.add(convertOrderItemFormToPojo(orderItemForm));

        return orderItemForm;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemUpdateForm update(OrderItemUpdateForm orderItemUpdateForm) throws ApiException {

        validate(orderItemUpdateForm, "Body values cannot be null");
        int orderId = orderItemServices.get(orderItemUpdateForm.getId()).getOrderId();
        if (checkOrderStatus(orderId)) {
            throw new ApiException("Cannot update as order already placed for order id : " + orderId);
        }
        updateInventoryQty(orderItemUpdateForm);

        orderItemServices.update(convertOrderItemFormToPojo(orderItemUpdateForm));

        return orderItemUpdateForm;
    }

    public Integer delete(Integer id) throws ApiException {

        int OrderId = orderItemServices.get(id).getOrderId();
        if (checkOrderStatus(OrderId)) {
            throw new ApiException("Cannot delete as order already placed for id : " + id);
        }
        OrderItemPojo ex = orderItemServices.get(id);
        InventoryPojo inv = inventoryServices.selectByProductId(ex.getProductId());
        if (isNull(inv)) {
            throw new ApiException("Product with given product id does not exist in the inventory, product id : " + ex.getProductId());
        }

        int availQty = inventoryServices.get(inv.getId()).getQuantity();
        InventoryUpdateForm invUpdate = new InventoryUpdateForm();
        invUpdate.setId(inv.getId());
        invUpdate.setQuantity(availQty + ex.getQuantity());
        inventoryServices.update(invUpdate);

        orderItemServices.delete(id);

        return id;
    }

    public OrderItemData get(Integer id) throws ApiException {
        return convertPojoToOrderItemData(orderItemServices.get(id));
    }

    @Transactional(rollbackOn = ApiException.class)
    private void validateOrderItemForm(OrderItemForm orderItemForm) throws ApiException {
        validate(orderItemForm, "body values cannot be null");

        if (orderItemServices.selectFromOrderIdProductId(orderItemForm.getOrderId(), orderItemForm.getProductId()) != null) {
            throw new ApiException("OrderItem already exist update that instead");
        }
        OrderPojo orderPojo = orderServices.get(orderItemForm.getOrderId());
        if (isNull(orderPojo)) {
            throw new ApiException("Order with this id does not exist, id : " + orderItemForm.getOrderId());
        }

        ProductPojo productPojo = productServices.get(orderItemForm.getProductId());
        if (isNull(productPojo)) {
            throw new ApiException("Product with this id does not exist, id : " + orderItemForm.getProductId());
        }

        int availQty = getAvailQty(orderItemForm.getProductId());
        if (orderItemForm.getQuantity() > availQty) {
            throw new ApiException("Selected quantity more than available quantity, available quantity only " + availQty);
        }
        int orderId = orderItemForm.getOrderId();
        if (checkOrderStatus(orderId)) {
            throw new ApiException("Cannot add as order already placed for order id : " + orderItemForm.getOrderId());
        }

    }

    private boolean checkOrderStatus(Integer orderId) throws ApiException {
        OrderPojo orderPojo = orderServices.get(orderId);
        return orderPojo.getOrderPlaced();
    }

    private Integer getAvailQty(Integer productId) throws ApiException {
        InventoryPojo inv = inventoryServices.selectByProductId(productId);
        if (isNull(inv)) {
            throw new ApiException("Product with this id does not exist in the inventory, id : " + productId);
        }

        return inventoryServices.get(inv.getId()).getQuantity();
    }

    @Transactional(rollbackOn = ApiException.class)
    private void updateInventoryQty(OrderItemUpdateForm orderItemUpdateForm) throws ApiException {
        if (orderItemUpdateForm.getQuantity() <= 0) {
            throw new ApiException("Quantity must be greater than 0");
        }
        OrderItemPojo orderItemExist = orderItemServices.get(orderItemUpdateForm.getId());

        InventoryPojo inv = inventoryServices.selectByProductId(orderItemExist.getProductId());
        if (isNull(inv)) {
            throw new ApiException("Product associated with given orderItem id does not exist in the inventory, product id : " + orderItemExist.getProductId());
        }

        int availQty = inventoryServices.get(inv.getId()).getQuantity();
        if (!(availQty + orderItemExist.getQuantity() >= orderItemUpdateForm.getQuantity())) {
            throw new ApiException("Selected quantity more than available quantity, available quantity only " + availQty);
        }

        InventoryUpdateForm invUpdate = new InventoryUpdateForm();
        invUpdate.setId(inv.getId());
        invUpdate.setQuantity(availQty + orderItemExist.getQuantity() - orderItemUpdateForm.getQuantity());
        inventoryServices.update(invUpdate);

    }


}
