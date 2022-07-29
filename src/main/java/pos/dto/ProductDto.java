package pos.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.model.ProductUpdateForm;
import pos.pojo.ProductPojo;
import pos.services.ProductServices;
import pos.spring.ApiException;

import java.util.ArrayList;
import java.util.List;

import static pos.util.DataUtil.checkNotNullUtil;
import static pos.util.DataUtil.normalizeUtil;

@Service
public class ProductDto {

    @Autowired
    private ProductServices service;

    public List<ProductData> getAll() throws ApiException{
        List<ProductPojo> p =  service.getAll();
        List<ProductData> pData = new ArrayList<>();
        for( ProductPojo pj : p){
            pData.add(convertPojoToProductForm(pj));
        }
        return pData;
    }

    public void add(ProductForm p) throws ApiException {
        checkNotNullUtil(p,"parameters in the Insert form cannot be null");
        normalizeUtil(p);
        service.add(p);
    }

    public void bulkAdd(List<ProductForm> bulkP) throws ApiException{
        service.bulkAdd(bulkP);
    }

    public ProductData get(int id) throws ApiException{
        return convertPojoToProductForm(service.get(id));
    }

    public void update(ProductUpdateForm p) throws ApiException{
        checkNotNullUtil(p,"parameters in the Update form cannot be null");
        normalizeUtil(p);
        service.update(p);
    }

    private ProductData convertPojoToProductForm(ProductPojo p){
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

}
