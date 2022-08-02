package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.InventoryDao;
import pos.dao.ProductDao;
import pos.model.ProductForm;
import pos.model.ProductUpdateForm;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pos.util.DataUtil.*;
import static pos.util.DataUtil.validateMRP;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductServices {

    @Autowired
    private ProductDao dao;

    public void add(ProductPojo productPojo) throws ApiException {
        normalize(productPojo);
        validateBarcode(productPojo.getBarcode());
        validateMRP(productPojo.getMrp());
        checkBarcodeExist(productPojo.getBarcode());
        dao.add(productPojo);
    }

    public void bulkAdd(List<ProductPojo> productPojoList) throws ApiException {
            for (ProductPojo productPojo : productPojoList) {
               add(productPojo);
            }
    }

    public List<ProductPojo> getAll() throws ApiException {
        return dao.selectAll();
    }

    public ProductPojo get(int id) throws ApiException {
        return getCheck(id);
    }

    public void update(ProductPojo productPojo) throws ApiException {
        normalize(productPojo);
        validateBarcode(productPojo.getBarcode());
        validateMRP(productPojo.getMrp());

        if(dao.selectByBarcodeNotEqualId(productPojo.getBarcode(),productPojo.getId())!=null){ //TODO change this function
            throw new ApiException("barcode " + productPojo.getBarcode() + " already exists");
        }
        updateUtil(productPojo);
    }

    public ProductPojo getCheck(int id) throws ApiException {
        ProductPojo productPojo = dao.select(id);
        if (productPojo == null) {
            throw new ApiException("Product with given id does not exist, id : " + id);
        }
        return productPojo;
    }

    public ProductPojo getCheckInPojo(int id) throws ApiException {
        ProductPojo productPojo = dao.select(id);
        if (productPojo == null) {
            throw new ApiException("product with given id does not exist, id : " + id);
        }
        return productPojo;
    }

    public boolean checkBrandExist(int brandId){
        return dao.selectByBrandId(brandId).size()>0;
    }

    public List<ProductPojo> getByBrand(String brand){
        return dao.selectByBrand(brand);
    }
    public List<ProductPojo> getByCategory(String category){
        return dao.selectByCategory(category);
    }
    public List<ProductPojo> getByBrandAndCategory(String brand, String category){
        return dao.selectByBrandAndCategory(brand,category);
    }
    public List<ProductPojo> selectByBrandId(Integer brandId){
        return dao.selectByBrandId(brandId);
    }

    public ProductPojo selectByBarcode(String barcode){ return dao.selectByBarcode(barcode);}
    private void updateUtil(ProductPojo productPojo) throws ApiException {

        ProductPojo productPojoUpdate = getCheckInPojo(productPojo.getId());
        productPojoUpdate.setBrandId(productPojo.getBrandId());
        productPojoUpdate.setBrand(productPojo.getBrand());
        productPojoUpdate.setCategory(productPojo.getCategory());
        productPojoUpdate.setBarcode(productPojo.getBarcode());
        productPojoUpdate.setMrp(productPojo.getMrp());
        productPojoUpdate.setName(productPojo.getName());

        dao.update(); //symbolic
    }
    private void checkBarcodeExist(String barcode) throws ApiException {
        if(dao.selectByBarcode(barcode)!=null){
            throw new ApiException("barcode "  + barcode +  " already exists");
        }
    }


}



