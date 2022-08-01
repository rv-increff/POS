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
        OrderPojo ex = new OrderPojo();
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
        ex.setTime(date);
        dao.add(ex);
    }

    public List<OrderPojo> getAll() throws ApiException {
        return  dao.selectAll();
    }

    public OrderPojo get(int id) throws ApiException {
        return getCheck(id);
    }


    public OrderPojo getCheck(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        return p;
    }

    public void updateOrderStatusPlaced(int id) throws ApiException {
        OrderPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        p.setOrderPlaced(true);
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
        p.setTime(date);
    }

    public List<OrderPojo> selectByFromTODate(ZonedDateTime from, ZonedDateTime to){
        return dao.selectByFromTODate(from,to);
    }

}
