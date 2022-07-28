package pos.dao;

import org.springframework.stereotype.Repository;
import pos.model.InventoryReport;
import pos.pojo.InventoryPojo;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao{
    private final static String SELECT_FROM_BARCODE = "select p from InventoryPojo p where barcode=:barcode";
    private final static String SELECT_FROM_PRODUCT_ID = "select p from InventoryPojo p where productId=:productId";
    private final static String SELECT_INVENTORY_REPORT = "select new " +
            "pos.model.InventoryReport(p.brand, p.category, i.quantity) from ProductPojo p, InventoryPojo i where " +
            "p.id=i.productId";

    public void add(InventoryPojo p){
        addAbs(p);
    }

    public InventoryPojo select(int id){
       return select(InventoryPojo.class,id);
    }

    public List<InventoryPojo> selectAll(){
        return selectAll(InventoryPojo.class);
    }

    public InventoryPojo selectFromBarcode(String barcode){
        TypedQuery<InventoryPojo> query = em().createQuery(SELECT_FROM_BARCODE,InventoryPojo.class);
        query.setParameter("barcode",barcode);
        return getSingle(query);
    }

    public InventoryPojo selectFromProductId(int productId){
        TypedQuery<InventoryPojo> query = em().createQuery(SELECT_FROM_PRODUCT_ID,InventoryPojo.class);
        query.setParameter("productId",productId);
        InventoryPojo p = getSingle(query);
        return p;
    }

    public List<InventoryReport> getInventoryReport(){
        TypedQuery<InventoryReport> query = em().createQuery(SELECT_INVENTORY_REPORT,InventoryReport.class);
        return query.getResultList();
    }

    public void update(){
        //symbolic
    }

}
