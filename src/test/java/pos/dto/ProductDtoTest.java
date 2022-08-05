package pos.dto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.BrandDao;
import pos.dao.ProductDao;
import pos.model.ProductData;
import pos.model.ProductForm;
import pos.model.ProductUpdateForm;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;
import pos.services.InventoryServices;
import pos.services.ProductServices;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class ProductDtoTest {

    @Autowired
    private ProductDto dto;
    @Autowired
    private ProductServices service;
    @Autowired
    private ProductDao dao;
    @Autowired
    private BrandDao bDao;
    @Autowired
    private InventoryServices iService;



    @Test
    public void getAll() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();

        List<ProductData> plist = dto.getAll();
        Assert.assertEquals(n,plist.size());
    }
    @Test
    public void get() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();
        List<ProductPojo> productPojoList = dao.selectAll();

        int index  = productPojoList.get(n-1).getId();
        ProductData p = dto.get(index);
        Assert.assertEquals(Optional.of(index),Optional.of(p.getId()));
        try {
            dto.get(index+1);
        }catch (ApiException e){
            Assert.assertEquals("Product with given id does not exist, id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void addEmptyObject() throws ApiException {
        ProductForm p = new ProductForm();
        try {
            dto.add(p);
        }catch (ApiException e){
            Assert.assertEquals("parameters in the Insert form cannot be null",e.getMessage());
        }

    }


    @Test
    public void addBrandError() throws ApiException {
        ProductForm p = new ProductForm();

        int prevSize = dao.selectAll().size();

        String category = getRandomString();
        String brand = getRandomString();
        String name = getRandomString();
        String barcode = getRandomString();
        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(barcode);
        p.setName(name);
        p.setMrp(mrp);
        try{
            dto.add(p);
        }catch (ApiException e){
            String err = p.getBrand() + " - " + p.getCategory() + " brand-category does not exist";
            Assert.assertEquals(err,e.getMessage());
        }

    }

    @Test
    public void addUniqueError(){

        ProductPojo pojo = daoInsertHelper();
        ProductForm p = new ProductForm();
        String category = getRandomString();
        String brand = getRandomString();
        String name = getRandomString();

        BrandPojo brandPojo = new BrandPojo();
        brandPojo.setBrand(brand);
        brandPojo.setCategory(category);
        bDao.add(brandPojo);


        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(pojo.getBarcode());
        p.setName(name);
        p.setMrp(mrp);
        try{
            dto.add(p);
        }catch (ApiException e){
            String err = "barcode "  + p.getBarcode() +  " already exists";
            Assert.assertEquals(err,e.getMessage());
        }

    }

    @Test
    public void addBarcodeFormatError(){
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);

        ProductForm p = new ProductForm();
        String name = getRandomString();
        double mrp = Math.random()*100%(20);

        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(getRandomString() + " #");
        p.setName(name);
        p.setMrp(mrp);

        try{
            dto.add(p);
        }catch (ApiException e){
            String err = "barcode "  + p.getBarcode() +  " not valid, barcode can only have alphanumeric values";
            Assert.assertEquals(err,e.getMessage());
        }
    }

    @Test
    public void add() throws ApiException {
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);

        ProductForm p = new ProductForm();
        String name = getRandomString();
        double mrp = Math.random()*100%(20);

        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(getRandomString());
        p.setName(name);
        p.setMrp(mrp);

        dto.add(p);

    }

    @Test
    public void bulkAddEmptyObject(){

        ProductForm p = new ProductForm();
        List<ProductForm> pList = new ArrayList<>();
        pList.add(p);

        try{
            dto.bulkAdd(pList);
        } catch (ApiException e) {
            String err = "Error : row -> " + 1 + " parameters in the Insert form cannot be null\n";
            Assert.assertEquals(err,e.getMessage());
        }

    }

    @Test
    public void bulkAddBarcodeUniqueError(){
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);
        ProductPojo pojo = daoInsertHelper();
        ProductForm p = new ProductForm();
        String name = getRandomString();

        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(pojo.getBarcode());
        p.setName(name);
        p.setMrp(mrp);

        List<ProductForm> pList = new ArrayList<>();
        pList.add(p);
        try{
            dto.bulkAdd(pList);
        }catch (ApiException e){
            String err = "Error : row -> " + 1 + " barcode "  + p.getBarcode() +  " already exists\n";
            Assert.assertEquals(err,e.getMessage());
        }
    }
    @Test
    public void bulkAddBarcodeBrandError(){
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        ProductForm p = new ProductForm();
        String name = getRandomString();
        String barcode = getRandomString();

        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(barcode);
        p.setName(name);
        p.setMrp(mrp);

        List<ProductForm> pList = new ArrayList<>();
        pList.add(p);
        try{
            dto.bulkAdd(pList);
        }catch (ApiException e){
            String err = "Error : row -> " + 1 + " " + p.getBrand() + " - " + p.getCategory() + " brand-category does not exist\n";
            Assert.assertEquals(err,e.getMessage());
        }
    }


    @Test
    public void bulkAddBarcodeFormatError(){
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);

        ProductForm p = new ProductForm();

        String name = getRandomString();
        double mrp = Math.random()*100%(20);

        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(getRandomString() + " #");
        p.setName(name);
        p.setMrp(mrp);

        List<ProductForm> pList = new ArrayList<>();
        pList.add(p);

        try{
            dto.bulkAdd(pList);
        }catch (ApiException e){
            String err = "Error : row -> " + 1 + " barcode "  + p.getBarcode() +  " not valid, barcode can only have alphanumeric values\n";
            Assert.assertEquals(err,e.getMessage());
        }

    }
    @Test
    public void bulkAdd() throws ApiException {
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);

        ProductForm p = new ProductForm();

        String name = getRandomString();
        double mrp = Math.random()*100%(20);


        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(getRandomString());
        p.setName(name);
        p.setMrp(mrp);

        List<ProductForm> pList = new ArrayList<>();
        pList.add(p);

        dto.bulkAdd(pList);


    }
    @Test
    public void update() throws ApiException {
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);

        ProductPojo p = daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(p.getBarcode());
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        dto.update(pUpdate);

    }
    @Test
    public void updateEmptyBodyError() throws ApiException {

        ProductUpdateForm pUpdate = new ProductUpdateForm();
        try{
        dto.update(pUpdate);
        }catch (ApiException e){
            String err = "parameters in the Update form cannot be null";
            Assert.assertEquals(err,e.getMessage());
        }

    }
    @Test
    public void updateBarcodeUniqueError() throws ApiException {

        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase();
        BrandPojo brandPojo =  new BrandPojo();
        brandPojo.setBrand(brand);
        brandPojo.setCategory(category);
        bDao.add(brandPojo);

        ProductPojo p = daoInsertHelper();
        ProductPojo p1 = daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(p1.getBarcode());
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        try{
        dto.update(pUpdate);
        }catch (ApiException e){
            String err = "barcode " + pUpdate.getBarcode() + " already exists";
            Assert.assertEquals(err,e.getMessage());
        }

    }
    @Test
    public void updateBarcodeSyntaxError() throws ApiException {
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);


        ProductPojo p = daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(p.getBarcode() + "# ");
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        try{
            dto.update(pUpdate);
        }catch (ApiException e){
            String err = "barcode "  + pUpdate.getBarcode() +  " not valid, barcode can only have alphanumeric values";
            Assert.assertEquals(err,e.getMessage());
        }

    }
    @Test
    public void updateNotInPojoError() throws ApiException {
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.add(pb);
        int id = 2;
        double mrp = Math.random()*100%(20);
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(getRandomString());
        pUpdate.setMrp(mrp);
        pUpdate.setId(id);

        try{
            dto.update(pUpdate);
        }catch (ApiException e){
            String err = "Product with given id does not exist, id : " + id;
            Assert.assertEquals(err,e.getMessage());
        }

    }
    @Test
    public void updateBrandError() {
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        ProductPojo p = daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(p.getBarcode());
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        try{
            dto.update(pUpdate);
        }catch (ApiException e){
            String err = pUpdate.getBrand() + " - " + pUpdate.getCategory() + " brand-category does not exist";
            Assert.assertEquals(err,e.getMessage());
        }

    }

//    @Test
//    public void checkIfBrandExist(){ //TODO add TEST key in function and check in terminal
//        Assert.assertTrue(!service.checkBrandExist(0));
//        String category = getRandomString().toLowerCase();
//        String brand = getRandomString().toLowerCase(Locale.ROOT);
//
//        //add brand category
//        BrandPojo pb = new BrandPojo();
//        pb.setBrand(brand);
//        pb.setCategory(category);
//        bDao.add(pb);
//
//        ProductPojo p = daoInsertHelper();
//        ProductUpdateForm pUpdate = new ProductUpdateForm();
//        pUpdate.setName(getRandomString());
//        pUpdate.setBrand(brand);
//        pUpdate.setCategory(category);
//        pUpdate.setBarcode(p.getBarcode());
//        pUpdate.setMrp(p.getMrp());
//        pUpdate.setId(p.getId());
//
//        Assert.assertTrue(service.checkBrandExist(dao.selectAll().get(0).getBrandId()));
//
//
//    }

    private ProductPojo daoInsertHelper(){
        ProductPojo p = new ProductPojo();
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase();
        String name = getRandomString().toLowerCase();
        String barcode = getRandomString();
        int brandPojoId = (int) (Math.random()*100%(20));
        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(barcode);
        p.setName(name);
        p.setBrandId(brandPojoId);
        p.setMrp(mrp);
        dao.add(p);
        return p;
    }

}
