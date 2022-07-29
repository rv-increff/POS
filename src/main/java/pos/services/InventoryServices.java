package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.InventoryDao;
import pos.dao.ProductDao;
import pos.model.InventoryForm;
import pos.model.InventoryUpdateForm;
import pos.pojo.InventoryPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pos.util.DataUtil.checkNotNullBulkUtil;

@Service
public class InventoryServices {
    @Autowired
    private InventoryDao dao;
    @Autowired
    private ProductDao bDao;
    @Autowired
    private OrderItemServices oService;

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryForm p) throws ApiException {
        if(dao.selectByBarcode(p.getBarcode())!=null){
            throw new ApiException("Inventory data already exist update the record instead");
        }
        ProductPojo pPojo = bDao.selectFromBarcode(p.getBarcode());
        if(pPojo==null){
            throw new ApiException("Product with this barcode does not exist");
        }
        int productId = pPojo.getId();
        if(p.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 0");
        }
        InventoryPojo ex = new InventoryPojo();
        ex.setBarcode(p.getBarcode());
        ex.setQuantity(p.getQuantity());
        ex.setProductId(productId);
        dao.add(ex);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void bulkAdd(List<InventoryForm> bulkP) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Set<String> barcodeSet = new HashSet<>();
        if(bulkP.size()==0){
            throw new ApiException("Empty data");
        }
        for(int i=0;i<bulkP.size();i++) {
            InventoryForm p = bulkP.get(i);
            if(!checkNotNullBulkUtil(p)){
                errorList.add("Error : row -> " + (i+1) + " barcode or quantity cannot be NULL");
                continue;
            }
            ProductPojo pPojo= bDao.selectFromBarcode(p.getBarcode());

            if(pPojo==null) {
                errorList.add("Error : row -> " + (i+1) +
                        " product with the barcode " + p.getBarcode() + " does not exist");
                continue;
            }

            if(dao.selectByBarcode(p.getBarcode())!=null){
                errorList.add("Error : row -> " + (i+1) +
                        " Inventory data already exist for barcode "+ p.getBarcode() +" update the record instead");
                continue;
            }
            if(p.getQuantity()<=0){
                errorList.add("Error : row -> " + (i+1) +
                        " Quantity must be greater than 1, quantity : " + p.getQuantity());
                continue;
            }
            if(barcodeSet.contains(p.getBarcode())){
                errorList.add("Error : row -> " + (i+1) +
                        " Barcode should not be repeated, barcode : " + p.getBarcode());
                continue;
            }else{
                barcodeSet.add(p.getBarcode());
            }
        }

        if(errorList.size()==0) {
            for (InventoryForm p : bulkP) {
                InventoryPojo ex = new InventoryPojo();
                ex.setBarcode(p.getBarcode());
                ex.setQuantity(p.getQuantity());
                int productId = bDao.selectFromBarcode(p.getBarcode()).getId();
                ex.setProductId(productId);
                dao.add(ex);
            }
            if(errorList.size()>0){
                String errorStr = "";
                for(String e : errorList){
                    errorStr += e + "\n";
                }
                throw new ApiException(errorStr);
            }
        }
        else{
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryPojo> getAll() throws ApiException {
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryUpdateForm p) throws ApiException {
        getCheck(p.getId());
        if(p.getQuantity()<0){
            throw new ApiException("Quantity must be greater than 0");
        }
        updateUtil(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryPojo getCheck(int id) throws ApiException {
        InventoryPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Inventory with given id does not exist, id : " + id);
        }
        return p;
    }

    private void updateUtil(InventoryUpdateForm p) {
        InventoryPojo ex = dao.select(p.getId());
        ex.setQuantity(p.getQuantity());
        dao.update(); //symbolic
    }


}
