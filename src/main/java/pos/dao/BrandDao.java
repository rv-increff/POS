package pos.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pos.pojo.BrandPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Repository
public class BrandDao extends AbstractDao{

    private static String delete_id = "delete from BrandPojo where id=:id"; //TODO wrong case
    private static String select_id = "select p from BrandPojo p where id=:id";
    private static String select_all = "select p from BrandPojo p";
    private static String unique = "select p from BrandPojo p where brand=:brand and category=:category";
    private static String check_brand = "select p from BrandPojo p where brand=:brand";
    private static String check_category = "select p from BrandPojo p where category=:category";

    @PersistenceContext
    EntityManager em; //TODO remove this to make abstarct dao more generic

    public void insert(BrandPojo p){
        em.persist(p);
    }

    public int delete(int id){  //TODO remove all delete
        Query query = em.createQuery(delete_id);
        query.setParameter("id",id);
        return query.executeUpdate();
    }

    public BrandPojo select(int id){
        TypedQuery<BrandPojo> query = em.createQuery(select_id,BrandPojo.class);
        query.setParameter("id",id);
        return getSingle(query);
    }

    public List<BrandPojo> selectAll(){
        TypedQuery<BrandPojo> query = em.createQuery(select_all,BrandPojo.class);
        return query.getResultList();
    }

    public boolean unique(String brand, String category){
        TypedQuery<BrandPojo> query = em.createQuery(unique,BrandPojo.class);
        query.setParameter("brand",brand);
        query.setParameter("category",category);
        return getSingle(query)==null;  //TODO dao cannot pass anything except pojo and id
    }
    public int getIdFromData(String brand, String category){
        TypedQuery<BrandPojo> query = em.createQuery(unique,BrandPojo.class);
        query.setParameter("brand",brand);
        query.setParameter("category",category);
        return getSingle(query)!=null ? getSingle(query).getId() : -1; //TODO improve code readability and enough spaces
        //TODO remove use of number if not needed (-1)
    }
    public boolean check_brand(String brand){
        TypedQuery<BrandPojo> query = em.createQuery(check_brand,BrandPojo.class);
        query.setParameter("brand",brand);
        return getSingle(query)!=null;


    }
    public boolean check_category(String category){ //TODO name
        TypedQuery<BrandPojo> query = em.createQuery(check_category,BrandPojo.class);
        query.setParameter("category",category);
        return getSingle(query)!=null;
    }

    public void update(){
        //symbolic
    }
}
