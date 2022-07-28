package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.OrderData;
import pos.pojo.OrderPojo;
import pos.services.OrderServices;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDto {

    @Autowired
    OrderServices services;

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderData> getAll() throws ApiException{
        List<OrderPojo> p =  services.getAll();
        List<OrderData> b = new ArrayList<OrderData>();
        for( OrderPojo pj : p){
            b.add(convertPojoToOrderData(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add() throws ApiException{
        services.add();
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderData get(int id) throws ApiException{
        return convertPojoToOrderData(services.get(id));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateOrderStatusPlaced(int id) throws ApiException{
        services.updateOrderStatusPlaced(id);
    }
    private OrderData convertPojoToOrderData(OrderPojo p){
        OrderData b = new OrderData();
        b.setId(p.getId());
        b.setTime(p.getTime());
        b.setOrderPlaced(p.getOrderPlaced());
        return b;
    }
}
