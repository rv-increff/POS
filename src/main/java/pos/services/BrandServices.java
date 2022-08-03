package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.pojo.BrandPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static pos.util.DataUtil.normalize;
import static pos.util.DataUtil.validate;

@Service
@Transactional(rollbackOn = ApiException.class)
public class BrandServices {

    @Autowired
    private BrandDao dao;

    //TODO service should deal with its own entity
//TODO general check file / validation check on dto and service specific on service
    //normalize in service

    public void add(BrandPojo brandPojo) throws ApiException {  //TODO add dto for conversion only pojo not form check on dto and service layer
        //TODO service layer response in pojo
        normalize(brandPojo);
        checkUnique(brandPojo);
        dao.add(brandPojo);
    }

    public void bulkAdd(List<BrandPojo> brandPojoList) throws ApiException {
        normalizeList(brandPojoList);
        checkAlreadyExistInBrandPojo(brandPojoList);
        for(BrandPojo brandPojo : brandPojoList){
            add(brandPojo);
        }
    }

    public List<BrandPojo> getAll() throws ApiException { //TODO will get and getall will be transactional?
        return dao.selectAll();
    }

    public BrandPojo get(Integer id) throws ApiException {
        return getCheck(id);
    }

    public void update(BrandPojo brandPojo) throws ApiException {
        validate(brandPojo,"brand or category cannot be null");
        normalize(brandPojo);
        checkUnique(brandPojo);

        BrandPojo exists = getCheck(brandPojo.getId());   //TODO do not make 1 use method
        exists.setBrand(brandPojo.getBrand());
        exists.setCategory(brandPojo.getCategory());
        dao.update(); //symbolic
    }

    public BrandPojo getCheck(Integer id) throws ApiException {
            BrandPojo brandPojo = dao.select(id);
            if (brandPojo == null) {
                throw new ApiException("Brand with given id does not exist ,id : " + id);
            }
            return brandPojo;
        }



    public BrandPojo selectByBrandCategory(String brand, String category){
        return dao.selectByBrandCategory(brand,category);
    }

    public BrandPojo selectByBrand(String brand){
        return dao.selectByBrand(brand);
    }

    public BrandPojo selectByCategory(String brand){
        return dao.selectByCategory(brand);
    }

    private void checkUnique(BrandPojo brandPojo) throws ApiException {
        if(selectByBrandCategory(brandPojo.getBrand(),brandPojo.getCategory())!=null){
            throw new ApiException(brandPojo.getBrand() + " - " +  brandPojo.getCategory() + " pair already exist");
        }
    }

    private void checkAlreadyExistInBrandPojo(List<BrandPojo> brandPojoList) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Integer row =1;
        for(BrandPojo brandPojo : brandPojoList) {
                if(selectByBrandCategory(brandPojo.getBrand(),brandPojo.getCategory())!=null) {
                    errorList.add("Error : row -> " + (row) + " "  + brandPojo.getBrand() +
                            " - " +  brandPojo.getCategory() + " pair already exist");
                }
            row++;
        }
        if(errorList.size()>0) {
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }

    private void normalizeList(List<BrandPojo> brandPojoList){
        for(BrandPojo brandPojo : brandPojoList){
            normalize(brandPojo);
        }
    }

}
