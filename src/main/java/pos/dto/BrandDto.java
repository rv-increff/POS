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

import static pos.util.DataUtil.checkNotNullUtil;
import static pos.util.DataUtil.normalizeUtil;

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
        service.add(p);
    }

    public void bulkAdd(List<BrandForm> brandFormList) throws ApiException{
        validBrandListCheck(brandFormList);
        service.bulkAdd(brandFormList);
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

    private void validBrandListCheck(List<BrandForm> brandFormList) throws ApiException {
        Set<String> brandSet = new HashSet<>();
        List<String> errorList = new ArrayList<>();

        for(int i=0;i<brandFormList.size();i++) {
            BrandForm brandForm = brandFormList.get(i);
            if (brandSet.contains(brandForm.getBrand() + brandForm.getCategory())) {
                errorList.add("Error : row -> " + (i + 1)
                        + " Brand-Category should not be repeated, Brand-category : "  //TODO shift to dto form check for duplicatu
                        + brandForm.getBrand() + "-" + brandForm.getCategory());
                continue;
            } else {
                brandSet.add(brandForm.getBrand() + brandForm.getCategory());
            }
        }
        if(errorList.size()==0){
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }
}
