package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.OrderItemDao;
import pos.pojo.OrderItemPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Objects.isNull;

@Transactional(rollbackOn = ApiException.class)
@Service //TODO add transaction on class level
public class OrderItemServices {

    @Autowired
    private OrderItemDao dao;

    public void add(OrderItemPojo orderItemPojo) throws ApiException {
        if (orderItemPojo.getQuantity() <= 0) {
            throw new ApiException("Quantity must be greater than 0");
        }
        dao.add(orderItemPojo);
    }

    public List<OrderItemPojo> getAll() throws ApiException {
        return dao.selectAll();
    }

    public OrderItemPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    public void update(OrderItemPojo orderItemPojo) throws ApiException {
        getCheck(orderItemPojo.getId());
        OrderItemPojo ex = dao.select(orderItemPojo.getId());
        ex.setQuantity(orderItemPojo.getQuantity());
        ex.setSellingPrice(orderItemPojo.getSellingPrice());
        dao.update(); //symbolic
    }

    public OrderItemPojo getCheck(Integer id) throws ApiException {
        OrderItemPojo orderItemPojo = dao.select(id);
        if (isNull(orderItemPojo)) {
            throw new ApiException("OrderItem with given id does not exist ,id : " + id);
        }
        return orderItemPojo;
    }

    public void delete(Integer id) throws ApiException {
        getCheck(id);
        dao.delete(id);
    }

    public boolean checkOrderItemWithProductId(Integer productId) {
        return isNull(dao.selectFromProductId(productId));
    }

    public List<OrderItemPojo> selectFromOrderIdList(List<Integer> orderIdList) {
        return dao.selectFromOrderIdList(orderIdList);
    }

    public OrderItemPojo selectFromOrderIdProductId(Integer orderId, Integer productId) {
        return dao.selectFromOrderIdProductId(orderId, productId);
    }

    public List<OrderItemPojo> selectFromOrderId(Integer orderId) {
        return dao.selectFromOrderId(orderId);
    }

}
