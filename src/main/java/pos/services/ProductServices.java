package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.ProductDao;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.Objects.isNull;
import static pos.util.DataUtil.*;

@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductServices {

    @Autowired
    private ProductDao dao;
//TODO format code
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

        ProductPojo productPojoUpdate = getCheckInPojo(productPojo.getId());
        productPojoUpdate.setBrandId(productPojo.getBrandId());
        productPojoUpdate.setBrand(productPojo.getBrand());
        productPojoUpdate.setCategory(productPojo.getCategory());
        productPojoUpdate.setBarcode(productPojo.getBarcode());
        productPojoUpdate.setMrp(productPojo.getMrp());
        productPojoUpdate.setName(productPojo.getName());

        dao.update(); //symbolic
    }

    public ProductPojo getCheck(Integer id) throws ApiException {
        ProductPojo productPojo = dao.select(id);
        if (isNull(productPojo)) {
            throw new ApiException("Product with given id does not exist, id : " + id);
        }
        return productPojo;
    }

    public ProductPojo getCheckInPojo(Integer id) throws ApiException { //TODO
        ProductPojo productPojo = dao.select(id);
        if (productPojo == null) {
            throw new ApiException("product with given id does not exist, id : " + id);
        }
        return productPojo;
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

    private void checkBarcodeExist(String barcode) throws ApiException {
        if(!isNull(dao.selectByBarcode(barcode))){
            throw new ApiException("barcode "  + barcode +  " already exists");
        }
    }


}



