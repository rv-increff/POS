package pos.dao;

import org.springframework.stereotype.Repository;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.pojo.OrderItemPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderItemDao extends AbstractDao{
        private static String delete_id = "delete from OrderItemPojo where id=:id";
        private static String select_id = "select p from OrderItemPojo p where id=:id";
        private static String select_all = "select p from OrderItemPojo p";
        private static String select_from_orderId = "select p from OrderItemPojo p where orderId=:orderId";
        private static String select_from_orderId_productId = "select p from OrderItemPojo p where orderId=:orderId and productId=:productId";
        private static String select_from_productId = "select p from OrderItemPojo p where productId=:productId";

        private String queryBuilder = "select new pos.model.SalesReport(p.brand, p.category,sum(oi.quantity) as quantity, sum(oi.quantity * oi.sellingPrice) as revenue) from ProductPojo p, OrderItemPojo oi, OrderPojo o where ";


        private String queryBuilderEnd =  " group by p.brand,p.category";
        @PersistenceContext
        EntityManager em;

        public void insert(OrderItemPojo p){
            em.persist(p);
        }

        public int delete(int id){
            Query query = em.createQuery(delete_id);
            query.setParameter("id",id);
            return query.executeUpdate();
        }

        public OrderItemPojo select(int id){
            TypedQuery<OrderItemPojo> query = em.createQuery(select_id, OrderItemPojo.class);
            query.setParameter("id",id);
            return getSingle(query);
        }

        public List<OrderItemPojo> selectAll(){
            TypedQuery<OrderItemPojo> query = em.createQuery(select_all, OrderItemPojo.class);
            return query.getResultList();
        }

        public List<OrderItemPojo> selectFromOrderId(int orderId){
            TypedQuery<OrderItemPojo> query = em.createQuery(select_from_orderId, OrderItemPojo.class);
            query.setParameter("orderId",orderId);
            return query.getResultList();
        }

        public boolean checkOrderExist(int orderId,int productId){
            TypedQuery<OrderItemPojo> query = em.createQuery(select_from_orderId_productId, OrderItemPojo.class);
            query.setParameter("orderId",orderId);
            query.setParameter("productId",productId);
            return getSingle(query)==null;
        }
        public boolean checkOrderExistForProductId(int productId){
            TypedQuery<OrderItemPojo> query = em.createQuery(select_from_productId, OrderItemPojo.class);
            query.setParameter("productId",productId);
            return getSingle(query)==null;
        }

        public List<SalesReport> getSalesReport(SalesReportForm s) throws ParseException {
            List<String> queryBuilderList = new ArrayList<String>();
            DateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
            queryBuilderList.add(" oi.productId=p.id and oi.orderId=o.id ");
            queryBuilderList.add(" o.orderPlaced=1 ");
            if(s.getFrom().toString()!=""){
                queryBuilderList.add(" o.time>=:fromTime ");

            }
             if(s.getTo().toString()!=""){
                queryBuilderList.add(" o.time<=:toTime ");

            }
            if(s.getBrand()!=""){
                queryBuilderList.add(" p.brand=:brand ");

            }
            if(s.getCategory()!=""){
                queryBuilderList.add(" p.category=:category ");

            }
            String queryBuilderFinal  = queryBuilder + queryBuilderList.stream().collect(Collectors.joining(" and ")) + queryBuilderEnd;

            TypedQuery<SalesReport> query = em.createQuery(queryBuilderFinal,SalesReport.class);
            if(s.getTo().toString()!=""){
                query.setParameter("toTime",sdf.parse(s.getTo()));
            }
            if(s.getFrom().toString()!=""){
                query.setParameter("fromTime",sdf.parse(s.getFrom()));
            }
            if(s.getBrand()!=""){
                query.setParameter("brand",s.getBrand());
            }
            if(s.getCategory()!=""){
                query.setParameter("category",s.getCategory());
            }
            System.out.println(query);
            return query.getResultList();
        }

        public void update(){
                //symbolic
            }

}
