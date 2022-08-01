package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.ProductDao;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.pojo.BrandPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pos.util.DataUtil.*;

@Service
public class BrandServices {

    @Autowired
    private BrandDao dao;
    @Autowired
    private ProductDao pDao;  //TODO service should deal with its own entity

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandPojo p) throws ApiException {  //TODO add dto for conversion only pojo not form check on dto and service layer
        if(dao.selectByBrandCategory(p.getBrand(),p.getCategory())!=null){
            throw new ApiException("Brand and category pair should be unique");
        }     //TODO service layer response in pojo
        BrandPojo bPojo = new BrandPojo();
        bPojo.setBrand(p.getBrand());
        bPojo.setCategory(p.getCategory());
        dao.add(bPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void bulkAdd(List<BrandPojo> brandPojoList) throws ApiException {
        for(BrandPojo brandPojo : brandPojoList){
            add(brandPojo);
        }
    }

    @Transactional(rollbackOn = ApiException.class)
    public List<BrandPojo> getAll() throws ApiException { //TODO will get and getall will be transactional?
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(BrandData p) throws ApiException {

        if(pDao.selectByBrandId(p.getId()).size()>0){
            throw new ApiException("cannot update " + p.getBrand() + " - " +  p.getCategory() + " as product for this exist");
        }
        if(dao.selectByBrandCategory(p.getBrand(),p.getCategory())!=null){
            throw new ApiException(p.getBrand() + " - " +  p.getCategory() + " pair should be unique");
        }
        updateUtil(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo getCheck(int id) throws ApiException {
            BrandPojo p = dao.select(id);
            if (p== null) {
                throw new ApiException("Brand with given id does not exist ,id : " + id);
            }
            return p;
        }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo getCheckInPojo(int id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Brand with given id does not exist ,id : " + id);
        }
        return p;
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo selectByBrandCategory(String brand, String category){
        return dao.selectByBrandCategory(brand,category);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo selectByBrand(String brand){
        return dao.selectByBrand(brand);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo selectByCategory(String brand){
        return dao.selectByCategory(brand);
    }


    private void updateUtil(BrandData p) throws ApiException {
        BrandPojo ex = getCheckInPojo(p.getId());
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        dao.update(); //symbolic
    }



}
