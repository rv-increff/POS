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
public class ProductDao extends AbstractDao{
    private static String delete_id = "delete from ProductPojo where id=:id";
    private static String select_id = "select p from ProductPojo p where id=:id";
    private static String select_all = "select p from ProductPojo p";
    private static String unique = "select p from ProductPojo p where barcode=:barcode";
    private static String unique_update = "select p from ProductPojo p where barcode=:barcode and id!=:id";

    private static String get_product_from_brand = "select p from ProductPojo p where brandPojoId=:brandPojoId";

    @PersistenceContext
    EntityManager em;

    public void insert(ProductPojo p){
        em.persist(p);
    }

    public int delete(int id){
        Query query = em.createQuery(delete_id);
        query.setParameter("id",id);
        return query.executeUpdate();
    }

    public ProductPojo select(int id){
        TypedQuery<ProductPojo> query = em.createQuery(select_id,ProductPojo.class);
        query.setParameter("id",id);
        return getSingle(query);
    }

    public List<ProductPojo> selectAll(){
        TypedQuery<ProductPojo> query = em.createQuery(select_all,ProductPojo.class);
        return query.getResultList();
    }

    public boolean unique(String barcode){
        TypedQuery<ProductPojo> query = em.createQuery(unique,ProductPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query)==null;
    }

    public boolean uniqueUpdate(String barcode,int id){
        TypedQuery<ProductPojo> query = em.createQuery(unique_update,ProductPojo.class);
        query.setParameter("barcode",barcode);
        query.setParameter("id",id);
        return getSingle(query)==null;

    }
    public int getIdFromBarcode(String barcode){
        TypedQuery<ProductPojo> query = em.createQuery(unique,ProductPojo.class);
        query.setParameter("barcode",barcode);
        ProductPojo p = getSingle(query);
        if(p==null)
            return -1;
        return p.getId();
    }
    public void update(){
        //symbolic
    }
    public int checkProductId(int id){
        TypedQuery<ProductPojo> query = em.createQuery(select_id,ProductPojo.class);
        query.setParameter("id",id);
        ProductPojo p = getSingle(query);
        if(p==null)return -1;
        return p.getId();
    }
    public boolean checkIfBrandExist(int brandId){
        TypedQuery<ProductPojo> query = em.createQuery(get_product_from_brand,ProductPojo.class);
        query.setParameter("brandPojoId",brandId);
        System.out.println(query.getResultList());
        return query.getResultList().size()>0;
    }
}
