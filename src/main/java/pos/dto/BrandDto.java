package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.pojo.BrandPojo;
import pos.services.BrandServices;
import pos.spring.ApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pos.util.DataUtil.*;

@Service
public class BrandDto {
    @Autowired
    private BrandServices service;

    public List<BrandData> getAll() throws ApiException {
        List<BrandPojo> p =  service.getAll();

        List<BrandData> b = new ArrayList<>();
        for( BrandPojo pj : p){
            b.add(convertPojoToBrandData(pj));
        }
        return b;
    }

    public void add(BrandForm p) throws ApiException{
        checkNotNullUtil(p,"brand or category cannot be null");
        normalizeUtil(p);
        service.add(convertBrandFormToPojo(p));
    }

    public void bulkAdd(List<BrandForm> brandFormList) throws ApiException{
        if(brandFormList.size()==0){
            throw new ApiException("Empty data");
        }
        checkValidBrandList(brandFormList);
        checkAlreadyExistInBrandPojo(brandFormList);
        List<BrandPojo> brandPojoList = new ArrayList<>();
        for(BrandForm brandForm : brandFormList){
            brandPojoList.add(convertBrandFormToPojo(brandForm));
        }
        service.bulkAdd(brandPojoList);
    }

    public BrandData get(int id) throws ApiException{
        return convertPojoToBrandData(service.get(id));
    }

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
    private BrandPojo convertBrandFormToPojo(BrandForm p){
        BrandPojo b = new BrandPojo();
        b.setBrand(p.getBrand());
        b.setCategory(p.getCategory());
        return b;
    }

    private void checkValidBrandList(List<BrandForm> brandFormList) throws ApiException {
        Set<String> brandSet = new HashSet<>();
        List<String> errorList = checkNotNullAndNormalize(brandFormList);

        for(int i=0;i<brandFormList.size();i++) {
            BrandForm brandForm = brandFormList.get(i);
            if (brandSet.contains(brandForm.getBrand() + brandForm.getCategory())) {
                errorList.add("Error : row -> " + (i + 1) + " Brand-Category should not be repeated, Brand-category : "  //TODO shift to dto form check for duplicatu
                        + brandForm.getBrand() + "-" + brandForm.getCategory());
                continue;
            } else {
                brandSet.add(brandForm.getBrand() + brandForm.getCategory());
            }
        }

        if(errorList.size()!=0){
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }
    private List<String> checkNotNullAndNormalize(List<BrandForm> brandFormList){
        List<String> errorList = new ArrayList<>();
        for(int i=0;i<brandFormList.size();i++){
            BrandForm brandForm = brandFormList.get(i);
            if(!checkNotNullBulkUtil(brandForm)){
                errorList.add("Error : row -> " + (i + 1) + " Brand-Category should not be null" );
            }
            normalizeUtil(brandForm);
        }
        return errorList;
    }

    private void checkAlreadyExistInBrandPojo(List<BrandForm> brandFormList) throws ApiException {
        List<String> errorList = new ArrayList<>();
        for(int i=0;i<brandFormList.size();i++) {
            BrandForm p = brandFormList.get(i);
            if(checkNotNullBulkUtil(p)) {
                normalizeUtil(p);
                if(service.selectByBrandCategory(p.getBrand(),p.getCategory())!=null) {
                    errorList.add("Error : row -> " + (i+1) + " "  + p.getBrand() +
                            " - " +  p.getCategory() + " pair should be unique");
                }
            }
            else {
                errorList.add("Error : row -> " + (i+1) + " brand or category cannot be empty");
            }
        }
        if(errorList.size()>0) {
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }
}
