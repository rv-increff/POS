package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.InventoryData;
import pos.model.InventoryForm;
import pos.model.InventoryUpdateForm;
import pos.pojo.InventoryPojo;
import pos.services.InventoryServices;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static pos.util.DataUtil.checkNotNullUtil;

@Service
public class InventoryDto {

    @Autowired
    private InventoryServices service;

    @Transactional(rollbackOn = ApiException.class)
    public List<InventoryData> getAll() throws ApiException{
        List<InventoryPojo> p =  service.getAll();
        List<InventoryData> b = new ArrayList<>();
        for( InventoryPojo pj : p){
            b.add(convertPojoToInventoryForm(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(InventoryForm p) throws ApiException{
        checkNotNullUtil(p,"Barcode or quantity cannot be NULL");
        service.add(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void bulkAdd(List<InventoryForm> bulkP) throws ApiException{
        service.bulkAdd(bulkP);
    }

    @Transactional(rollbackOn = ApiException.class)
    public InventoryData get(int id) throws ApiException{
        return convertPojoToInventoryForm(service.get(id));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(InventoryUpdateForm p) throws ApiException{
        checkNotNullUtil(p,"Quantity cannot be NULL");
        service.update(p);
    }

    private InventoryData convertPojoToInventoryForm(InventoryPojo p){
        InventoryData b = new InventoryData();
        b.setId(p.getId());
        b.setBarcode(p.getBarcode());
        b.setQuantity(p.getQuantity());
        b.setProductId(p.getProductId());
        return b;
    }
}
