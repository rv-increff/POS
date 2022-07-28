package pos.dao;

import org.springframework.stereotype.Repository;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderItemDao extends AbstractDao{
    private final static String DELETE = "delete from OrderItemPojo where id=:id";
    private final static String SELECT_FROM_ORDER_ID = "select p from OrderItemPojo p where orderId=:orderId";
    private final static String SELECT_FROM_ORDER_ID_PRODUCT_ID =
            "select p from OrderItemPojo p where orderId=:orderId and productId=:productId";
    private final static String SELECT_FROM_PRODUCT_ID = "select p from OrderItemPojo p where productId=:productId";
    private final static String QUERY_BUILDER =
            "select new pos.model.SalesReport(p.brand, p.category,sum(oi.quantity) " +
            "as quantity, sum(oi.quantity * oi.sellingPrice) as revenue) " +
            "from ProductPojo p, OrderItemPojo oi, OrderPojo o where ";
    private final static String QUERY_BUILDER_SUFFIX =  " group by p.brand,p.category";

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
            TypedQuery<OrderItemPojo> query = em().createQuery(SELECT_FROM_ORDER_ID, OrderItemPojo.class);
            query.setParameter("orderId",orderId);
            return query.getResultList();
        }

    public OrderItemPojo selectFromOrderIdProductId(int orderId, int productId){
        TypedQuery<OrderItemPojo> query = em().createQuery(SELECT_FROM_ORDER_ID_PRODUCT_ID, OrderItemPojo.class);
        query.setParameter("orderId",orderId);
        query.setParameter("productId",productId);
        return getSingle(query);
    }

    public OrderItemPojo selectFromProductId(int productId){
        TypedQuery<OrderItemPojo> query = em().createQuery(SELECT_FROM_PRODUCT_ID, OrderItemPojo.class);
        query.setParameter("productId",productId);
        return getSingle(query);
    }

    public List<SalesReport> getSalesReport(SalesReportForm s) throws ParseException {
        List<String> queryBuilderList = new ArrayList<>();
        DateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        queryBuilderList.add(" oi.productId=p.id and oi.orderId=o.id ");
        queryBuilderList.add(" o.orderPlaced=1 ");
        if(!Objects.equals(s.getFrom(), "")){
            queryBuilderList.add(" o.time>=:fromTime ");

        }
         if(!Objects.equals(s.getTo(), "")){
            queryBuilderList.add(" o.time<=:toTime ");

        }
        if(!Objects.equals(s.getBrand(), "")){
            queryBuilderList.add(" p.brand=:brand ");

        }
        if(!Objects.equals(s.getCategory(), "")){
            queryBuilderList.add(" p.category=:category ");

        }
        String queryBuilderFinal  = QUERY_BUILDER + String.join(" and ", queryBuilderList) + QUERY_BUILDER_SUFFIX;

        TypedQuery<SalesReport> query = em().createQuery(queryBuilderFinal,SalesReport.class);
        if(!Objects.equals(s.getTo(), "")){
//            query.setParameter("toTime",sdf.parse(s.getTo()));
            query.setParameter("toTime", ZonedDateTime.parse(s.getTo()));
        }
        if(!Objects.equals(s.getFrom(), "")){
//            query.setParameter("fromTime",sdf.parse(s.getFrom()));
            query.setParameter("fromTime",ZonedDateTime.parse(s.getFrom()));
        }
        if(!Objects.equals(s.getBrand(), "")){
            query.setParameter("brand",s.getBrand());
        }
        if(!Objects.equals(s.getCategory(), "")){
            query.setParameter("category",s.getCategory());
        }
        return query.getResultList();
    }

    public void update(){
            //symbolic
        }

}
