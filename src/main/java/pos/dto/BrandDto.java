package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.pojo.BrandPojo;
import pos.services.BrandServices;
import pos.services.ProductServices;
import pos.spring.ApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pos.util.DataUtil.validate;
import static pos.util.HelperUtil.*;

@Service
public class BrandDto {
    @Autowired
    private BrandServices brandServices;

    @Autowired
    private ProductServices productService;

    private static void checkDuplicates(List<BrandForm> brandFormList) throws ApiException {
        Set<String> brandSet = new HashSet<>();
        List<String> errorList = new ArrayList<>();
        Integer row = 1;
        for (BrandForm brandForm : brandFormList) { //TODO do not use index for loop
            if (brandSet.contains(brandForm.getBrand() + brandForm.getCategory())) {
                errorList.add("Error : row -> " + (row) + " Brand-Category should not be repeated, Brand-category : "  //TODO shift to dto form check for duplicatu
                        + brandForm.getBrand() + "-" + brandForm.getCategory());
                continue;
            } else {
                brandSet.add(brandForm.getBrand() + brandForm.getCategory());
            }
            row++;
        }
        if (errorList.size() != 0) {
            String errorStr = "";
            for (String e : errorList) {
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }

    public List<BrandData> getAll() throws ApiException {
        List<BrandPojo> brandPojoList = brandServices.getAll();
        List<BrandData> brandDataList = new ArrayList<>();
        for (BrandPojo brandPojo : brandPojoList) {
            brandDataList.add(convertPojoToBrandData(brandPojo));
        }

        return brandDataList;
    }

    public BrandForm add(BrandForm brandForm) throws ApiException {
        validate(brandForm, "brand or category cannot be null");  //TODO change name to validate
        brandServices.add(convertBrandFormToPojo(brandForm));
        return brandForm;
    }

    public Integer bulkAdd(List<BrandForm> brandFormList) throws ApiException {
        if (brandFormList.size() == 0) { //TODO use collection
            throw new ApiException("Empty data");
        }

        validateList(brandFormList);
        checkDuplicates(brandFormList);

        List<BrandPojo> brandPojoList = new ArrayList<>();
        for (BrandForm brandForm : brandFormList) {
            brandPojoList.add(convertBrandFormToPojo(brandForm));
        }
        brandServices.bulkAdd(brandPojoList);

        return brandFormList.size();
    }

    public BrandData get(Integer id) throws ApiException {
        return convertPojoToBrandData(brandServices.get(id));
    }

    public BrandData update(BrandData brandData) throws ApiException {
        validate(brandData, "brand or category cannot be null");
        if (!CollectionUtils.isEmpty(productService.selectByBrandId(brandData.getId()))) {
            throw new ApiException("cannot update " + brandData.getBrand() + " - " + brandData.getCategory() + " as product for this exist");
        }

        brandServices.update(convertBrandDataToPojo(brandData));

        return brandData;
    }


}
