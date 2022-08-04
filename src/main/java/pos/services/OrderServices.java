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

import static java.util.Objects.isNull;

@Service
@Transactional(rollbackOn = ApiException.class)
public class OrderServices {

    @Autowired
    private OrderDao dao;

    public ZonedDateTime add() throws ApiException {
        OrderPojo orderPojo = new OrderPojo();
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
        orderPojo.setTime(date);
        dao.add(orderPojo);

        return date;
    }

    public List<OrderPojo> getAll() throws ApiException {
        return dao.selectAll();
    }

    public OrderPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    public OrderPojo getCheck(Integer id) throws ApiException {
        OrderPojo orderPojo = dao.select(id);
        if (isNull(orderPojo)) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        return orderPojo;
    }

    public void updateOrderStatusPlaced(Integer id) throws ApiException {
        OrderPojo orderPojo = dao.select(id);
        if (isNull(orderPojo)) {
            throw new ApiException("Order with given id does not exist, id : " + id);
        }
        orderPojo.setOrderPlaced(true);
        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
        orderPojo.setTime(date);
    }

    public List<OrderPojo> selectByFromToDate(ZonedDateTime from, ZonedDateTime to) {
        return dao.selectByFromTODate(from, to);
    }

}
