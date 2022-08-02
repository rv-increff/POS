package pos.util;

import pos.model.*;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import java.util.ArrayList;
import java.util.List;

import static pos.util.DataUtil.checkNotNullBulkUtil;

public class HelperUtil {
    public static ProductData convertPojoToProductData(ProductPojo p){  //TODO put all static function to util
        ProductData b = new ProductData();
        b.setId(p.getId());
        b.setBrand(p.getBrand());
        b.setCategory(p.getCategory());
        b.setBrandPojoId(p.getBrandId());
        b.setBarcode(p.getBarcode());
        b.setMrp(p.getMrp());
        b.setName(p.getName());
        return b;
    }
    public static ProductPojo convertFormToProductPojo(ProductForm p){
        ProductPojo productPojo = new ProductPojo();
        productPojo.setBrand(p.getBrand());
        productPojo.setCategory(p.getCategory());
        productPojo.setBarcode(p.getBarcode());
        productPojo.setMrp(p.getMrp());
        productPojo.setName(p.getName());
        return productPojo;
    }
    public static ProductPojo convertFormToProductPojo(ProductUpdateForm productUpdateForm){
        ProductPojo productPojo = new ProductPojo();
        productPojo.setId(productUpdateForm.getId());
        productPojo.setBrand(productUpdateForm.getBrand());
        productPojo.setCategory(productUpdateForm.getCategory());
        productPojo.setBarcode(productUpdateForm.getBarcode());
        productPojo.setMrp(productUpdateForm.getMrp());
        productPojo.setName(productUpdateForm.getName());
        return productPojo;
    }
    public static void validateList(List<BrandForm> brandFormList) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Integer row = 1;
        for(BrandForm brandForm : brandFormList){
            if(!checkNotNullBulkUtil(brandForm)){
                errorList.add("Error : row -> " + (row) + " brand or category cannot be empty");
            }
            row ++;
        }
        if(errorList.size()>0) {
            String errorStr = "";
            for(String e : errorList){
                errorStr += e + "\n";
            }
            throw new ApiException(errorStr);
        }
    }
    public static BrandData convertPojoToBrandData(BrandPojo brandPojo){
        BrandData b = new BrandData();
        b.setId(brandPojo.getId());
        b.setBrand(brandPojo.getBrand());
        b.setCategory(brandPojo.getCategory());
        return b;
    }
    public static BrandPojo convertBrandFormToPojo(BrandForm brandForm){
        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brandForm.getBrand());
        brandPojo.setCategory(brandForm.getCategory());
        return brandPojo;
    }
    public static BrandPojo convertBrandDataToPojo(BrandData brandData){
        BrandPojo b = new BrandPojo();
        b.setId(brandData.getId());
        b.setBrand(brandData.getBrand());
        b.setCategory(brandData.getCategory());
        return b;
    }
}
