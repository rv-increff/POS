package pos.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.model.BrandForm;
import pos.model.BrandData;
import pos.pojo.BrandPojo;
import pos.util.StringUtil;

@Service
public class BrandServices {

    @Autowired
    private BrandDao dao;

    @Autowired
    private ProductServices pServices;

    @Transactional(rollbackOn = ApiException.class)
    public void add(BrandForm p) throws ApiException {
        nullCheck(p); //TODO check not null not nullcheck
        normalizeInsert(p); //TODO normalize
        if(!dao.unique(p.getBrand(),p.getCategory())){  //TODO
            throw new ApiException("Brand and category pair should be unique");
        }
        BrandPojo ex = new BrandPojo();
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        dao.insert(ex);
    }
    @Transactional(rollbackOn = ApiException.class)
    public void bulkAdd(List<BrandForm> bulkP) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Set<String> brandSet = new HashSet<>();
        if(bulkP.size()==0){
            throw new ApiException("Empty data");
        }
        for(int i=0;i<bulkP.size();i++) {
            BrandForm p = bulkP.get(i);
            if(!nullCheckBulk(p)) {
                normalizeInsert(p);
                if(!dao.unique(p.getBrand(),p.getCategory())) {
                    errorList.add("Error : row -> " + (i+1) + " "  + p.getBrand() + " - " +  p.getCategory() + " pair should be unique");
                }
                if(brandSet.contains(p.getBrand() + p.getCategory())){
                    errorList.add("Error : row -> " + (i+1) + " Brand-Category should not be repeated, Brand-category : " + p.getBrand() + "-"+  p.getCategory());
                    continue;
                }else{
                    brandSet.add(p.getBrand() + p.getCategory());
                }

            }else {
                errorList.add("Error : row -> " + (i+1) + " brand or category cannot be empty");
            }

        }
        if(errorList.size()==0) {
            for (BrandForm p : bulkP) {
                BrandPojo ex = new BrandPojo();
                ex.setBrand(p.getBrand());
                ex.setCategory(p.getCategory());
                dao.insert(ex);
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
    public List<BrandData> getAll() throws ApiException {
        List<BrandPojo> p =  dao.selectAll();
        List<BrandData> b = new ArrayList<BrandData>();
        for( BrandPojo pj : p){
            b.add(convertPojoToBrandForm(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandData get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(BrandData p) throws ApiException {
        nullCheck(p);
        normalizeUpdate(p);
        System.out.println(p.getBrand() +" "+ p.getCategory());
        if(!dao.unique(p.getBrand(),p.getCategory())){
            throw new ApiException(p.getBrand() + " - " +  p.getCategory() + " pair should be unique");
        }
        updateUtil(p);


    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandData getCheck(int id) throws ApiException {
            BrandPojo p = dao.select(id);
            if (p== null) {
                throw new ApiException("Brand with given id does not exist ,id : " + id);
            }
            return convertPojoToBrandForm(p);
        }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo getCheckInPojo(int id) throws ApiException {
        BrandPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Brand with given id does not exist ,id : " + id);
        }
        return p;
    }
    protected static void normalizeInsert(BrandForm p) {
        p.setBrand(StringUtil.toLowerCase(p.getBrand()));
        p.setCategory(StringUtil.toLowerCase(p.getCategory()));
    }

    protected static void normalizeUpdate(BrandData p) {

        p.setBrand(StringUtil.toLowerCase(p.getBrand()));
        p.setCategory(StringUtil.toLowerCase(p.getCategory()));

    }

    private void updateUtil(BrandData p) throws ApiException {
        BrandPojo ex = getCheckInPojo(p.getId());
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        dao.update(); //symbolic
    }

    private BrandData convertPojoToBrandForm(BrandPojo p){
        BrandData b = new BrandData();
        b.setId(p.getId());
        b.setBrand(p.getBrand());
        b.setCategory(p.getCategory());
        return b;
    }

    private void nullCheck(BrandForm form) throws ApiException { //TODO make validation util with static class check null check null and check not null(object ,message)
        if(form.getCategory()==null || form.getBrand()==null){
            throw new ApiException("brand or category cannot be null");
        }
    }
    private void nullCheck(BrandData form) throws ApiException {
        if(form.getCategory()==null || form.getBrand()==null){
            throw new ApiException("brand or category cannot be null");
        }

    }

    private boolean nullCheckBulk(BrandForm form) throws ApiException {
       return (form.getCategory()==null || form.getBrand()==null);

    }

}
