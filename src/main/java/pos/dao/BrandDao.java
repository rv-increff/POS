package pos.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import pos.pojo.BrandPojo;

import javax.persistence.TypedQuery;

@Repository
public class BrandDao extends AbstractDao{


    private final static String SELECT_FROM_BRAND_CATEGORY = "select p from BrandPojo p where " +
            "brand=:brand and category=:category";
    private final static String SELECT_FROM_BRAND = "select p from BrandPojo p where brand=:brand";
    private final static String SELECT_FROM_CATEGORY = "select p from BrandPojo p where category=:category";

    public void add(BrandPojo p){
        addAbs(p);
    }

    public BrandPojo select(int id){
        return select(BrandPojo.class,id);
    }

    public List<BrandPojo> selectAll(){
        return selectAll(BrandPojo.class);
    }

    public BrandPojo selectFromBrandCategory(String brand, String category){
        TypedQuery<BrandPojo> query = em().createQuery(SELECT_FROM_BRAND_CATEGORY,BrandPojo.class);
        query.setParameter("brand",brand);
        query.setParameter("category",category);
        return getSingle(query);
    }

    public BrandPojo selectBrand(String brand){
        TypedQuery<BrandPojo> query = em().createQuery(SELECT_FROM_BRAND,BrandPojo.class);
        query.setParameter("brand",brand);
        return getSingle(query);
    }

    public BrandPojo selectCategory(String category){
        TypedQuery<BrandPojo> query = em().createQuery(SELECT_FROM_CATEGORY,BrandPojo.class);
        query.setParameter("category",category);
        return getSingle(query);
    }

    public void update(){
        //symbolic
    }
}
