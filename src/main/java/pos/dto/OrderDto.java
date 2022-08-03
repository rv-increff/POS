package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.OrderData;
import pos.pojo.OrderPojo;
import pos.services.OrderServices;
import pos.spring.ApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDto {

    @Autowired
    OrderServices services;
    public List<OrderData> getAll() throws ApiException{
        List<OrderPojo> p =  services.getAll();
        List<OrderData> b = new ArrayList<>();
        for( OrderPojo pj : p){
            b.add(convertPojoToOrderData(pj));
        }
        return b;
    }

    public void add() throws ApiException{
        services.add();
    }

    public OrderData get(int id) throws ApiException{
        return convertPojoToOrderData(services.get(id));
    }

    public void updateOrderStatusPlaced(int id) throws ApiException{
        services.updateOrderStatusPlaced(id);
    }
    private OrderData convertPojoToOrderData(OrderPojo p){
        OrderData b = new OrderData();
        b.setId(p.getId());
        b.setTime(p.getTime().toLocalDateTime().toString());
        b.setOrderPlaced(p.getOrderPlaced());
        return b;
    }
}
