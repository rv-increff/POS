package pos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.dao.BrandDao;
import pos.dao.InventoryDao;
import pos.dao.ProductDao;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.model.ProductUpdateForm;
import pos.pojo.ProductPojo;
import pos.util.StringUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        nullCheck(p);
        normalizeInsert(p);
        if(!dao.unique(p.getBarcode())){
            throw new ApiException("barcode "  + p.getBarcode() +  " already exists");
        }
        int brandPojoId = bDao.getIdFromData(p.getBrand(),p.getCategory());
        if(brandPojoId==-1){
            throw new ApiException(p.getBrand() + " - " + p.getCategory() + " brand-category does not exist");
        }
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
        Matcher matcher = pattern.matcher(p.getBarcode());
        if(!matcher.find()){
            throw new ApiException("barcode "  + p.getBarcode() +  " not valid, barcode can only have alphanumeric values");
        }
        Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
        matcher = numP.matcher(p.getMrp().toString());
        if(!matcher.find()){
            throw new ApiException("mrp "  + p.getMrp() +  " not valid, mrp should be a positive number");
        }
        ProductPojo ex = new ProductPojo();
        ex.setBrandPojoId(brandPojoId);
        ex.setBrand(p.getBrand());
        ex.setCategory(p.getCategory());
        ex.setBarcode(p.getBarcode());
        ex.setMrp(p.getMrp());
        ex.setName(p.getName());
        dao.insert(ex);
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

            if(p.getBarcode()==null || p.getBrand()==null || p.getMrp()==null || p.getCategory()==null || p.getName()==null){
                errorList.add("Error : row -> " + (i+1) + " parameters in the Insert form cannot be null");
                continue;
            }
            Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
            Matcher matcher = numP.matcher(p.getMrp().toString());
            if(!matcher.find()){
                errorList.add("Error : row -> " + (i+1) + " mrp "  + p.getMrp() +  " not valid, mrp should be a positive number");
                continue;
            }
            normalizeInsert(p);
            if(!dao.unique(p.getBarcode())) {
                errorList.add("Error : row -> " + (i+1) + " barcode " + p.getBarcode() + " already exists");
                continue;
            }
            int brandPojoId = bDao.getIdFromData(p.getBrand(),p.getCategory());

            if(brandPojoId==-1){
                errorList.add("Error : row -> " + (i+1) + " " + p.getBrand() + " - " + p.getCategory() + " brand-category does not exist");
                continue;
            }
            Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
            matcher = pattern.matcher(p.getBarcode());
            if(!matcher.find()){
                errorList.add("Error : row -> " + (i+1) + " barcode "  + p.getBarcode() +  " not valid, barcode can only have alphanumeric values");
                continue;
            }
            if(barcodeSet.contains(p.getBarcode())){
                errorList.add("Error : row -> " + (i+1) + " Barcode should not be repeated, barcode : " + p.getBarcode());
                continue;
            }else{
                barcodeSet.add(p.getBarcode());
            }
        }
        if(errorList.size()==0) {
            for (ProductForm p : bulkP) {
                int brandPojoId = bDao.getIdFromData(p.getBrand(),p.getCategory());
                ProductPojo ex = new ProductPojo();
                ex.setBrandPojoId(brandPojoId);
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
                errorStr += e + "<br>";
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
        nullCheckUpdate(p);
        normalizeUpdate(p);
        if(!dao.uniqueUpdate(p.getBarcode(),p.getId())){
            throw new ApiException("barcode " + p.getBarcode() + " already exists");
        }
        Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
        Matcher matcher = pattern.matcher(p.getBarcode());
        if(!matcher.find()){
            throw new ApiException("barcode "  + p.getBarcode() +  " not valid, barcode can only have alphanumeric values");
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
        return dao.checkIfBrandExist(brandId);
    }
    protected static void normalizeInsert(ProductForm p) {
        p.setBrand(StringUtil.toLowerCase(p.getBrand()));
        p.setCategory(StringUtil.toLowerCase(p.getCategory()));
        p.setName(StringUtil.toLowerCase(p.getName()));
    }
    protected static void normalizeUpdate(ProductUpdateForm p) {

        p.setBrand(StringUtil.toLowerCase(p.getBrand()));
        p.setCategory(StringUtil.toLowerCase(p.getCategory()));
        p.setName(StringUtil.toLowerCase(p.getName()));

    }
    private void updateUtil(ProductUpdateForm p) throws ApiException {
        ProductPojo ex = getCheckInPojo(p.getId());
        int brandPojoId = bDao.getIdFromData(p.getBrand(),p.getCategory());
        if(brandPojoId==-1){
            throw new ApiException(p.getBrand() + " - " + p.getCategory() + " brand-category does not exist");
        }
        Pattern numP = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]*$");
        Matcher matcher = numP.matcher(p.getMrp().toString());
        if(!matcher.find()){
            throw new ApiException("mrp "  + p.getMrp() +  " not valid, mrp should be a positive number");
        }
        ex.setBrandPojoId(brandPojoId);
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
        b.setBrandPojoId(p.getBrandPojoId());
        b.setBarcode(p.getBarcode());
        b.setMrp(p.getMrp());
        b.setName(p.getName());
        return b;
    }
    private static void nullCheck(ProductForm p) throws ApiException {
        if(p.getBarcode()==null || p.getBrand()==null || p.getMrp()==null || p.getCategory()==null || p.getName()==null){
            throw new ApiException("parameters in the Insert form cannot be null");
        }
    }
    private static void nullCheckUpdate(ProductUpdateForm p) throws ApiException {
        if(p.getBarcode()==null || p.getBrand()==null || p.getMrp()==null || p.getCategory()==null || p.getName()==null ){
            throw new ApiException("parameters in the Update form cannot be null");
        }
    }

}



