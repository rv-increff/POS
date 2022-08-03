package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.OrderItemPojo;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Repository
public class OrderItemDao extends AbstractDao{
    private final static String DELETE = "delete from OrderItemPojo where id=:id";
    private final static String SELECT_BY_ORDER_ID = "select p from OrderItemPojo p where orderId=:orderId";
    private final static String SELECT_BY_ORDER_ID_PRODUCT_ID =
            "select p from OrderItemPojo p where orderId=:orderId and productId=:productId";
    private final static String SELECT_BY_PRODUCT_ID = "select p from OrderItemPojo p where productId=:productId";

    private final static String SELECT_BY_ORDER_ID_LIST = "select p from OrderItemPojo p where orderId in :orderIdList";

    public void add(OrderItemPojo p){
        addAbs(p);
    }

    public OrderItemPojo select(int id){
        return select(OrderItemPojo.class,id);
    }

    public List<OrderItemPojo> selectAll(){
      return selectAll(OrderItemPojo.class);
    }

    public int delete(int id){
        Query query = em().createQuery(DELETE);
        query.setParameter("id",id);
        return query.executeUpdate();
    }

    public List<OrderItemPojo> selectFromOrderId(int orderId){
            TypedQuery<OrderItemPojo> query = em().createQuery(SELECT_BY_ORDER_ID, OrderItemPojo.class);
            query.setParameter("orderId",orderId);
            return query.getResultList();
        }

    public OrderItemPojo selectFromOrderIdProductId(int orderId, int productId){
        TypedQuery<OrderItemPojo> query = em().createQuery(SELECT_BY_ORDER_ID_PRODUCT_ID, OrderItemPojo.class);
        query.setParameter("orderId",orderId);
        query.setParameter("productId",productId);
        return getSingle(query);
    }

    public OrderItemPojo selectFromProductId(int productId){
        TypedQuery<OrderItemPojo> query = em().createQuery(SELECT_BY_PRODUCT_ID, OrderItemPojo.class);
        query.setParameter("productId",productId);
        return getSingle(query);
    }

    public void update(){
            //symbolic
        }
    public List<OrderItemPojo> selectFromOrderIdList(List<Integer> orderIdList){
        TypedQuery<OrderItemPojo> query = em().createQuery(SELECT_BY_ORDER_ID_LIST, OrderItemPojo.class);
        query.setParameter("orderIdList",orderIdList);
        return query.getResultList();
    }

}
