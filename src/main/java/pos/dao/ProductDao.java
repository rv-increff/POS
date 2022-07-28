package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.ProductPojo;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao{
    private final static String SELECT_FROM_BARCODE = "select p from ProductPojo p where barcode=:barcode";
    private final static String SELECT_FROM_BARCODE_AND_NOT_EQUAL_ID = "select p from ProductPojo p where " +
            "barcode=:barcode and id!=:id";
    private final static String SELECT_FROM_BRAND_ID = "select p from ProductPojo p where brandId=:brandId";
    public void insert(ProductPojo p){
        addAbs(p);
    }
    public ProductPojo select(int id){
        return select(ProductPojo.class,id);
    }
    public List<ProductPojo> selectAll(){
        return selectAll(ProductPojo.class);
    }

    public ProductPojo selectFromBarcode(String barcode){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_FROM_BARCODE,ProductPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query);
    }

    public ProductPojo selectFromBarcodeNotEqualId(String barcode, int id){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_FROM_BARCODE_AND_NOT_EQUAL_ID,ProductPojo.class);
        query.setParameter("barcode",barcode);
        query.setParameter("id",id);
        return getSingle(query);
    }
    public void update(){
        //symbolic
    }

    public List<ProductPojo> selectFromBrand(int brandId){
        TypedQuery<ProductPojo> query = em().createQuery(SELECT_FROM_BRAND_ID,ProductPojo.class);
        query.setParameter("brandId",brandId);
        return query.getResultList();
    }
}
