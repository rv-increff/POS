package pos.dao;

import org.springframework.stereotype.Repository;
import pos.model.InventoryReport;
import pos.pojo.InventoryPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao{

    private static String delete_id = "delete from InventoryPojo where id=:id";
    private static String select_id = "select p from InventoryPojo p where id=:id";
    private static String select_all = "select p from InventoryPojo p";
    private static String unique = "select p from InventoryPojo p where barcode=:barcode";
    private static String select_from_prod = "select p from InventoryPojo p where productId=:productId";

    private static String inventory_report = "select new pos.model.InventoryReport(p.brand, p.category, i.quantity) from ProductPojo p, InventoryPojo i where p.id=i.productId";

    @PersistenceContext
    EntityManager em;

    public void insert(InventoryPojo p){
        em.persist(p);
    }

    public int delete(int id){
        Query query = em.createQuery(delete_id);
        query.setParameter("id",id);
        return query.executeUpdate();
    }

    public InventoryPojo select(int id){
        TypedQuery<InventoryPojo> query = em.createQuery(select_id,InventoryPojo.class);
        query.setParameter("id",id);
        return getSingle(query);
    }

    public List<InventoryPojo> selectAll(){
        TypedQuery<InventoryPojo> query = em.createQuery(select_all,InventoryPojo.class);
        return query.getResultList();
    }

    public boolean unique(String barcode){
        TypedQuery<InventoryPojo> query = em.createQuery(unique,InventoryPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query)==null;
//        return query.getResultList().size()==0;
    }

    public int getIdFromProductId(int productId){
        TypedQuery<InventoryPojo> query = em.createQuery(select_from_prod,InventoryPojo.class);
        query.setParameter("productId",productId);
        InventoryPojo p = getSingle(query);
        if (p==null){
            return -1;
        }
        return p.getId();
    }

    public List<InventoryReport> getInventoryReport(){
        TypedQuery<InventoryReport> query = em.createQuery(inventory_report,InventoryReport.class);
        return query.getResultList();
    }

    public void update(){
        //symbolic
    }

}
