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
    private ProductDao pDao;

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandForm p) throws ApiException {  //TODO add dto for conversion only pojo not form check on dto and service layer
        if(dao.selectFromBrandCategory(p.getBrand(),p.getCategory())!=null){
            throw new ApiException("Brand and category pair should be unique");
        }
        BrandPojo bPojo = new BrandPojo();
        bPojo.setBrand(p.getBrand());
        bPojo.setCategory(p.getCategory());
        dao.add(bPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void bulkAdd(List<BrandForm> brandPojoList) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Set<String> brandSet = new HashSet<>();
        if(brandPojoList.size()==0){
            throw new ApiException("Empty data");
        }
        for(int i=0;i<brandPojoList.size();i++) {
            BrandForm p = brandPojoList.get(i);
            if(checkNotNullBulkUtil(p)) {
                normalizeUtil(p);
                if(dao.selectFromBrandCategory(p.getBrand(),p.getCategory())!=null) {
                    errorList.add("Error : row -> " + (i+1) + " "  + p.getBrand() +
                            " - " +  p.getCategory() + " pair should be unique");
                }
                if(brandSet.contains(p.getBrand() + p.getCategory())){
                    errorList.add("Error : row -> " + (i+1)
                            + " Brand-Category should not be repeated, Brand-category : "  //TODO shift to dto form check for duplicatu
                            + p.getBrand() + "-"+  p.getCategory());
                    continue;
                }
                else{
                    brandSet.add(p.getBrand() + p.getCategory());
                }

            }
            else {
                errorList.add("Error : row -> " + (i+1) + " brand or category cannot be empty");
            }
//TODO if lse ladder issue
            //TODO reduce function size
        }
        if(errorList.size()==0) {
            for (BrandForm p : brandPojoList) {
                BrandPojo bPojo = new BrandPojo();
                bPojo.setBrand(p.getBrand());
                bPojo.setCategory(p.getCategory());
                dao.add(bPojo);
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
    public List<BrandPojo> getAll() throws ApiException {
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(BrandData p) throws ApiException {
        checkNotNullUtil(p,"brand or category cannot be null");
        normalizeUtil(p);
        if(pDao.selectFromBrand(p.getId()).size()>0){
            throw new ApiException("cannot update " + p.getBrand() + " - " +  p.getCategory() + " as product for this exist");
        }
        if(dao.selectFromBrandCategory(p.getBrand(),p.getCategory())!=null){
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

    private void updateUtil(BrandData p) throws ApiException {
        BrandPojo ex = getCheckInPojo(p.getId());
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        dao.update(); //symbolic
    }



}
