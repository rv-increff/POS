package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao{

    private static String select_id = "select p from OrderPojo p where id=:id";
    private static String select_all = "select p from OrderPojo p";

    @PersistenceContext
    EntityManager em;

    public void insert(OrderPojo p){
        em.persist(p);
    }

    public OrderPojo select(int id){
        TypedQuery<OrderPojo> query = em.createQuery(select_id,OrderPojo.class);
        query.setParameter("id",id);
        return getSingle(query);
    }

    public List<OrderPojo> selectAll(){
        TypedQuery<OrderPojo> query = em.createQuery(select_all,OrderPojo.class);
        return query.getResultList();
    }
    public int checkOrderId(int id){
        TypedQuery<OrderPojo> query = em.createQuery(select_id,OrderPojo.class);
        query.setParameter("id",id);
        OrderPojo p = getSingle(query);
        if(p==null)return -1;

        return p.getId();
    }
}
