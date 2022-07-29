package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.BrandPojo;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class BrandDao extends AbstractDao{

    private final static String SELECT_BY_BRAND_CATEGORY = "select p from BrandPojo p where " +
            "brand=:brand and category=:category";
    private final static String SELECT_BY_BRAND = "select p from BrandPojo p where brand=:brand"; //TODO select by
    private final static String SELECT_BY_CATEGORY = "select p from BrandPojo p where category=:category";

    public void add(BrandPojo p){
        this.add(p);
    }

    public BrandPojo select(int id){
        return select(BrandPojo.class,id);
    }

    public List<BrandPojo> selectAll(){
        return selectAll(BrandPojo.class);
    }

    public BrandPojo selectByBrandCategory(String brand, String category){ //TODO by not
        TypedQuery<BrandPojo> query = em().createQuery(SELECT_BY_BRAND_CATEGORY,BrandPojo.class);
        query.setParameter("brand",brand);
        query.setParameter("category",category);
        return getSingle(query);
    }

    public BrandPojo selectByBrand(String brand){
        TypedQuery<BrandPojo> query = em().createQuery(SELECT_BY_BRAND,BrandPojo.class);
        query.setParameter("brand",brand);
        return getSingle(query);
    }

    public BrandPojo selectByCategory(String category){
        TypedQuery<BrandPojo> query = em().createQuery(SELECT_BY_CATEGORY,BrandPojo.class);
        query.setParameter("category",category);
        return getSingle(query);
    }

    public void update(){
        //symbolic
    }
}
