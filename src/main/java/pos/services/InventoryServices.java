package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.*;
import pos.dao.InventoryDao;
import pos.model.InventoryForm;
import pos.model.InventoryData;
import pos.model.InventoryUpdateForm;
import pos.pojo.InventoryPojo;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        nullCheck(p);
        if(!dao.unique(p.getBarcode())){
            throw new ApiException("Inventory data already exist update the record instead");
        }
        int productId = bDao.getIdFromBarcode(p.getBarcode());
        if(productId==-1){
            throw new ApiException("Product with this barcode does not exist");
        }
        if(p.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 1");
        }
        InventoryPojo ex = new InventoryPojo();
        ex.setBarcode(p.getBarcode());
        ex.setQuantity(p.getQuantity());
        ex.setProductId(productId);
        dao.insert(ex);
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
            if(p.getBarcode()==null || p.getQuantity()==null){
                errorList.add("Error : row -> " + (i+1) + " barcode or quantity cannot be NULL");
                continue;
            }
            int productId = bDao.getIdFromBarcode(p.getBarcode());
            if(productId==-1) {
                errorList.add("Error : row -> " + (i+1) + " product with the barcode " + p.getBarcode() + " does not exist");
                continue;
            }
            if(!dao.unique(p.getBarcode())){
                errorList.add("Error : row -> " + (i+1) + " Inventory data already exist for barcode "+ p.getBarcode() +" update the record instead");
                continue;
            }
            if(p.getQuantity()<=0){
                errorList.add("Error : row -> " + (i+1) + " Quantity must be greater than 1, quantity : " + p.getQuantity());
                continue;
            }
            if(barcodeSet.contains(p.getBarcode())){
                errorList.add("Error : row -> " + (i+1) + " Barcode should not be repeated, barcode : " + p.getBarcode());
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
                int productId = bDao.getIdFromBarcode(p.getBarcode());
                ex.setProductId(productId);
                dao.insert(ex);
            }
            if(errorList.size()>0){
                String errorStr = "";
                for(String e : errorList){
                    errorStr += e + "<br>";
                }
                throw new ApiException(errorStr);
            }
        }
        else{
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "<br>";
            }
            throw new ApiException(errorStr);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryData> getAll() throws ApiException {
        List<InventoryPojo> p =  dao.selectAll();
        List<InventoryData> b = new ArrayList<InventoryData>();
        for( InventoryPojo pj : p){
            b.add(convertPojoToInventoryForm(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryUpdateForm p) throws ApiException {
        nullCheckUpdate(p);
        getCheck(p.getId());
        if(p.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 1");
        }
        updateUtil(p);


    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData getCheck(int id) throws ApiException {
        InventoryPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Inventory with given id does not exist, id : " + id);
        }
        return convertPojoToInventoryForm(p);
    }
    @Transactional(rollbackOn = ApiException.class)
    public boolean checkOrderOfInv(int id) throws ApiException {
        InventoryData p = getCheck(id);
        int productId = p.getProductId();
        return oService.checkOrderItemWithProductId(productId);


    }
    private void updateUtil(InventoryUpdateForm p) throws ApiException {
        InventoryPojo ex = dao.select(p.getId());
        ex.setQuantity(p.getQuantity());
        dao.update(); //symbolic
    }

    private InventoryData convertPojoToInventoryForm(InventoryPojo p){
        InventoryData b = new InventoryData();
        b.setId(p.getId());
        b.setBarcode(p.getBarcode());
        b.setQuantity(p.getQuantity());
        b.setProductId(p.getProductId());
        return b;
    }

    private void nullCheck(InventoryForm p) throws ApiException {
        if(p.getBarcode()==null || p.getQuantity()==null){
            throw new ApiException("Barcode or quantity cannot be NULL");
        }
    }
    private void nullCheckUpdate(InventoryUpdateForm p) throws ApiException {
        if(p.getQuantity()==null){
            throw new ApiException("Quantity cannot be NULL");
        }
    }




}
