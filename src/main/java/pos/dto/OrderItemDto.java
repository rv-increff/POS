package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.OrderItemData;
import pos.model.OrderItemForm;
import pos.model.OrderItemUpdateForm;
import pos.pojo.OrderItemPojo;
import pos.services.OrderItemServices;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemDto {

    @Autowired
    OrderItemServices services;

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> getAll() throws ApiException{
        List<OrderItemPojo> p =  services.getAll();
        List<OrderItemData> b = new ArrayList<>();
        for( OrderItemPojo pj : p){
            b.add(convertPojoToOrderForm(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderItemData> getOrderItemForOrder(int orderId) throws ApiException{
        List<OrderItemPojo> orderList = services.getOrderItemForOrder(orderId);
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for(OrderItemPojo i : orderList){
            OrderItemData j = convertPojoToOrderForm(i);
            orderItemDataList.add(j);
        }
        return orderItemDataList;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(OrderItemForm p) throws ApiException{
        services.add(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(OrderItemUpdateForm p) throws ApiException{
        services.update(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void delete(int id) throws ApiException{
        services.delete(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderItemData get(int id) throws ApiException{
        return convertPojoToOrderForm(services.get(id));
    }

    private OrderItemData convertPojoToOrderForm(OrderItemPojo p){
        OrderItemData b = new OrderItemData();
        b.setId(p.getId());
        b.setQuantity(p.getQuantity());
        b.setSellingPrice(p.getSellingPrice());
        b.setProductId(p.getProductId());
        b.setOrderId(p.getOrderId());
        return b;
    }
}
