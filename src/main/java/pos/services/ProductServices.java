package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.InventoryDao;
import pos.dao.ProductDao;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.model.ProductUpdateForm;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;
import pos.util.StringUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pos.util.DataUtil.*;

@Service
public class ProductServices {

    @Autowired
    private ProductDao dao;
    @Autowired
    private BrandDao bDao;
    @Autowired
    private InventoryDao iDao;


    @Transactional(rollbackOn = ApiException.class)
    public void add(ProductForm p) throws ApiException {
        checkNotNullUtil(p,"parameters in the Insert form cannot be null");
        normalizeUtil(p);
        if(dao.selectFromBarcode(p.getBarcode())!=null){
            throw new ApiException("barcode "  + p.getBarcode() +  " already exists");
        }
        BrandPojo bPojo= bDao.selectFromBrandCategory(p.getBrand(),p.getCategory());
        if(bPojo==null){
            throw new ApiException(p.getBrand() + " - " + p.getCategory() + " brand-category does not exist");
        }
        int brandId = bPojo.getId();
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
        Matcher matcher = pattern.matcher(p.getBarcode());
        if(!matcher.find()){
            throw new ApiException("barcode "  + p.getBarcode() +
                    " not valid, barcode can only have alphanumeric values");
        }
        Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
        matcher = numP.matcher(p.getMrp().toString());
        if(!matcher.find()){
            throw new ApiException("mrp "  + p.getMrp() +  " not valid, mrp should be a positive number");
        }
        ProductPojo pPojo = new ProductPojo();
        pPojo.setBrandId(brandId);
        pPojo.setBrand(p.getBrand());
        pPojo.setCategory(p.getCategory());
        pPojo.setBarcode(p.getBarcode());
        pPojo.setMrp(p.getMrp());
        pPojo.setName(p.getName());
        dao.insert(pPojo);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void bulkAdd(List<ProductForm> bulkP) throws ApiException {
        List<String> errorList = new ArrayList<>();
        Set<String> barcodeSet = new HashSet<>();
        if(bulkP.size()==0){
            throw new ApiException("Empty data");
        }
        for(int i=0;i<bulkP.size();i++) {
            ProductForm p = bulkP.get(i);
            if(!checkNotNullBulkUtil(p)){
                errorList.add("Error : row -> " + (i+1) + " parameters in the Insert form cannot be null");
                continue;
            }
            Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
            Matcher matcher = numP.matcher(p.getMrp().toString());
            if(!matcher.find()){
                errorList.add("Error : row -> " + (i+1) + " mrp "  + p.getMrp() +
                        " not valid, mrp should be a positive number");
                continue;
            }
            normalizeUtil(p);
            if(dao.selectFromBarcode(p.getBarcode())!=null) {
                errorList.add("Error : row -> " + (i+1) + " barcode " + p.getBarcode() + " already exists");
                continue;
            }
            BrandPojo bPojo = bDao.selectFromBrandCategory(p.getBrand(),p.getCategory());
            if(bPojo==null){
                errorList.add("Error : row -> " + (i+1) + " " + p.getBrand() + " - " + p.getCategory() +
                        " brand-category does not exist");
                continue;
            }
            Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
            matcher = pattern.matcher(p.getBarcode());
            if(!matcher.find()){
                errorList.add("Error : row -> " + (i+1) + " barcode "  + p.getBarcode() +
                        " not valid, barcode can only have alphanumeric values");
                continue;
            }
            if(barcodeSet.contains(p.getBarcode())){
                errorList.add("Error : row -> " + (i+1) + " Barcode should not be repeated, barcode : " +
                        p.getBarcode());
                continue;
            }else{
                barcodeSet.add(p.getBarcode());
            }
        }
        if(errorList.size()==0) {
            for (ProductForm p : bulkP) {
                int brandPojoId = bDao.selectFromBrandCategory(p.getBrand(),p.getCategory()).getId();
                ProductPojo ex = new ProductPojo();
                ex.setBrandId(brandPojoId);
                ex.setBrand(p.getBrand());
                ex.setCategory(p.getCategory());
                ex.setBarcode(p.getBarcode());
                ex.setMrp(p.getMrp());
                ex.setName(p.getName());
                dao.insert(ex);
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
    public List<ProductData> getAll() throws ApiException {
        List<ProductPojo> p =  dao.selectAll();
        List<ProductData> b = new ArrayList<ProductData>();
        for( ProductPojo pj : p){
            b.add(convertPojoToProductForm(pj));
        }
        return b;
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData get(int id) throws ApiException {
        return getCheck(id);
    }

    @Transactional(rollbackOn = ApiException.class)
    public void update(ProductUpdateForm p) throws ApiException {
        checkNotNullUtil(p,"parameters in the Update form cannot be null");
        normalizeUtil(p);
        if(dao.selectFromBarcodeNotEqualId(p.getBarcode(),p.getId())!=null){
            throw new ApiException("barcode " + p.getBarcode() + " already exists");
        }
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
        Matcher matcher = pattern.matcher(p.getBarcode());
        if(!matcher.find()){
            throw new ApiException("barcode "  + p.getBarcode() +
                    " not valid, barcode can only have alphanumeric values");
        }
        updateUtil(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductData getCheck(int id) throws ApiException {
        ProductPojo p = dao.select(id);
        if (p== null) {
            throw new ApiException("Product with given id does not exist, id : " + id);
        }
        return convertPojoToProductForm(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public ProductPojo getCheckInPojo(int id) throws ApiException {
        ProductPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("product with given id does not exist, id : " + id);
        }
        return p;
    }

    @Transactional(rollbackOn = ApiException.class)
    public boolean checkIfBrandExist(int brandId){
        return dao.selectFromBrand(brandId).size()>0;
    }

    private void updateUtil(ProductUpdateForm p) throws ApiException {
        ProductPojo ex = getCheckInPojo(p.getId());
        BrandPojo bPojo = bDao.selectFromBrandCategory(p.getBrand(),p.getCategory());
        if(bPojo==null){
            throw new ApiException(p.getBrand() + " - " + p.getCategory() + " brand-category does not exist");
        }
        int brandId = bPojo.getId();
        Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
        Matcher matcher = numP.matcher(p.getMrp().toString());
        if(!matcher.find()){
            throw new ApiException("mrp "  + p.getMrp() +  " not valid, mrp should be a positive number");
        }
        ex.setBrandId(brandId);
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        ex.setBarcode(p.getBarcode());
        ex.setMrp(p.getMrp());
        ex.setName(p.getName());
        dao.update(); //symbolic
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



