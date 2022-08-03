package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.OrderDao;
import pos.pojo.OrderPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderServices {

    @Autowired
    private OrderDao dao;

    public void add() throws ApiException {
        OrderPojo orderPojo = new OrderPojo();
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
        orderPojo.setTime(date);
        dao.add(orderPojo);
    }

    public List<OrderPojo> getAll() throws ApiException {
        return  dao.selectAll();
    }

    public OrderPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }


    public OrderPojo getCheck(Integer id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        return p;
    }

    public void updateOrderStatusPlaced(Integer id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        p.setOrderPlaced(true);
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
        p.setTime(date);
    }

    public List<OrderPojo> selectByFromToDate(ZonedDateTime from, ZonedDateTime to){
        return dao.selectByFromTODate(from,to);
    }

}
