package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.InventoryDao;
import pos.model.InventoryUpdateForm;
import pos.pojo.InventoryPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional(rollbackOn = ApiException.class)
@Service
public class InventoryServices {
    @Autowired
    private InventoryDao inventoryDao;

    public void add(InventoryPojo inventoryPojo) throws ApiException {
        if(inventoryDao.selectByBarcode(inventoryPojo.getBarcode())!=null){
            throw new ApiException("Inventory data already exist update the record instead");
        }
        if(inventoryPojo.getQuantity()<=0){
            throw new ApiException("Quantity must be greater than 0");
        }
        inventoryDao.add(inventoryPojo);
    }

    public void bulkAdd(List<InventoryPojo> inventoryPojoList) throws ApiException {
        List<String> errorList = new ArrayList<>();

        Integer row = 1;
        for(InventoryPojo inventoryPojo : inventoryPojoList) {
            if(inventoryDao.selectByBarcode(inventoryPojo.getBarcode())!=null){
                errorList.add("Error : row -> " + row +
                        " Inventory data already exist for barcode "+ inventoryPojo.getBarcode() +" update the record instead");
            }
            else if(inventoryPojo.getQuantity()<=0){
                errorList.add("Error : row -> " + row +
                        " Quantity must be greater than 0, quantity : " + inventoryPojo.getQuantity());
                continue;
            }
            row++;
        }

        if(errorList.size()==0) {
            for (InventoryPojo inventoryPojo : inventoryPojoList) {
                inventoryDao.add(inventoryPojo);
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

    public List<InventoryPojo> getAll() throws ApiException {
        return inventoryDao.selectAll();
    }

    public InventoryPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    public void update(InventoryUpdateForm inventoryUpdateForm) throws ApiException {
        getCheck(inventoryUpdateForm.getId());
        if(inventoryUpdateForm.getQuantity()<0){
            throw new ApiException("Quantity must be greater than 0");
        }
        updateUtil(inventoryUpdateForm);
    }

    public InventoryPojo getCheck(Integer id) throws ApiException {
        InventoryPojo p = inventoryDao.select(id);
        if (p == null) {
            throw new ApiException("Inventory with given id does not exist, id : " + id);
        }
        return p;
    }

    public InventoryPojo selectByBarcode(String barcode){
        return inventoryDao.selectByBarcode(barcode);
    }

    public InventoryPojo selectByProductId(Integer productId){
        return inventoryDao.selectByProductId(productId);
    }
    private void updateUtil(InventoryUpdateForm p) {
        InventoryPojo inventoryPojo = inventoryDao.select(p.getId());
        inventoryPojo.setQuantity(p.getQuantity());
        inventoryDao.update(); //symbolic
    }


}
