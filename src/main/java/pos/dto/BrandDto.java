package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.pojo.BrandPojo;
import pos.services.BrandServices;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static pos.util.DataUtil.checkNotNullUtil;
import static pos.util.DataUtil.normalizeUtil;

@Service
public class BrandDto {
    @Autowired
    private BrandServices service;

    @Transactional(rollbackOn = ApiException.class)
    public List<BrandData> getAll() throws ApiException {
        List<BrandPojo> p =  service.getAll();
        List<BrandData> b = new ArrayList<BrandData>();
        for( BrandPojo pj : p){
            b.add(convertPojoToBrandData(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandForm p) throws ApiException{
        checkNotNullUtil(p,"brand or category cannot be null");
        normalizeUtil(p);
        service.add(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void bulkAdd(List<BrandForm> bulkP) throws ApiException{
        service.bulkAdd(bulkP);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandData get(int id) throws ApiException{
        return convertPojoToBrandData(service.get(id));
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(BrandData p) throws ApiException{
        service.update(p);
    }

    private BrandData convertPojoToBrandData(BrandPojo p){
        BrandData b = new BrandData();
        b.setId(p.getId());
        b.setBrand(p.getBrand());
        b.setCategory(p.getCategory());
        return b;
    }
}
