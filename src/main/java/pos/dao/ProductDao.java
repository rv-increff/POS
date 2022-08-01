package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.ProductPojo;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao{
    private final static String SELECT_BY_BARCODE = "select p from ProductPojo p where barcode=:barcode";
    private final static String SELECT_BY_BARCODE_AND_NOT_EQUAL_ID = "select p from ProductPojo p where " +
            "barcode=:barcode and id!=:id";
    //TODO change query
    private final static String SELECT_BY_BRAND_ID = "select p from ProductPojo p where brandId=:brandId";
    private final static String SELECT_BY_BRAND = "select p from ProductPojo p where brand=:brand";
    private final static String SELECT_BY_CATEGORY = "select p from ProductPojo p where category=:category";
    private final static String SELECT_BY_BRAND_CATEGORY = "select p from ProductPojo p where category=:category and brand=:brand";


    public void add(ProductPojo p){
        addAbs(p);
    }
    public ProductPojo select(int id){
        return select(ProductPojo.class,id);
    }
    public List<ProductPojo> selectAll(){
        return selectAll(ProductPojo.class);
    }

    public ProductPojo selectByBarcode(String barcode){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_BY_BARCODE,ProductPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query);
    }

    public ProductPojo selectByBarcodeNotEqualId(String barcode, int id){ //TODO remove query
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_BY_BARCODE_AND_NOT_EQUAL_ID,ProductPojo.class);
        query.setParameter("barcode",barcode);
        query.setParameter("id",id);
        return getSingle(query); //TODO change query
    }
    public void update(){
        //symbolic
    }

    public List<ProductPojo> selectByBrandId(int brandId){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_BY_BRAND_ID,ProductPojo.class);
        query.setParameter("brandId",brandId);
        return query.getResultList();
    }

    public List<ProductPojo> selectByBrand(String brand){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_BY_BRAND,ProductPojo.class);
        query.setParameter("brand",brand);
        return query.getResultList();
    }
    public List<ProductPojo> selectByCategory(String category){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_BY_CATEGORY,ProductPojo.class);
        query.setParameter("category",category);
        return query.getResultList();
    }
    public List<ProductPojo> selectByBrandAndCategory(String brand,String category){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_BY_BRAND_CATEGORY,ProductPojo.class);
        query.setParameter("category",category);
        query.setParameter("brand",brand);
        return query.getResultList();
    }

}
