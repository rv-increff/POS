package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.OrderPojo;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao{
    private final static String SELECT_BY_FROM_TO_DATE = "select p from OrderPojo p where time>=:from and time<=:to";

    public void add(OrderPojo p){
        addAbs(p);
    }

    public OrderPojo select(int id){
        return select(OrderPojo.class,id);
    }

    public List<OrderPojo> selectAll(){
        return selectAll(OrderPojo.class);
    }

    public List<OrderPojo> selectByFromTODate(ZonedDateTime from, ZonedDateTime to){
        TypedQuery<OrderPojo> query = em().createQuery(SELECT_BY_FROM_TO_DATE,OrderPojo.class);
        query.setParameter("from",from);
        query.setParameter("to",to);
        return query.getResultList();
    }

}
