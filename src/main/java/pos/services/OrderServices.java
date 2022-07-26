package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.OrderDao;
import pos.model.OrderData;
import pos.pojo.OrderPojo;

import javax.transaction.Transactional;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServices {

    @Autowired
    private OrderDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void add() throws ApiException {
        OrderPojo ex = new OrderPojo();
        java.util.Date date = new java.util.Date();
        ex.setTime(date);
        dao.insert(ex);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderData> getAll() throws ApiException {
        List<OrderPojo> p =  dao.selectAll();
        List<OrderData> b = new ArrayList<OrderData>();
        for( OrderPojo pj : p){
            b.add(convertPojoToOrderData(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderData get(int id) throws ApiException {
        return getCheck(id);
    }

    private OrderData convertPojoToOrderData(OrderPojo p){
        OrderData b = new OrderData();
        b.setId(p.getId());
        b.setTime(p.getTime());
        b.setOrderPlaced(p.isOrderPlaced());
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderData getCheck(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        return convertPojoToOrderData(p);
    }
    @Transactional(rollbackOn = ApiException.class)
    public void updateOrderStatusPlaced(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Order with given id does not exist");
        }
        p.setOrderPlaced(true);
    }

}
