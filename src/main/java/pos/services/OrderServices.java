package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.OrderDao;
import pos.model.OrderData;
import pos.pojo.OrderPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
        dao.add(ex);
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<OrderPojo> getAll() throws ApiException {
        return  dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo get(int id) throws ApiException {
        return getCheck(id);
    }



    @Transactional(rollbackOn = ApiException.class)
    public OrderPojo getCheck(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        return p;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateOrderStatusPlaced(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        p.setOrderPlaced(true);
    }

}
