package pos.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.BrandDao;
import pos.dao.ProductDao;
import pos.model.*;
import pos.pojo.BrandPojo;
import pos.pojo.ProductPojo;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class ProductServiceTest {

    @Resource
    private ProductServices services;
    @Resource
    private ProductDao dao;
    @Resource
    private BrandDao bDao;
    @Resource
    private InventoryServices iService;



    @Test
    public void getAll() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();

        List<ProductData> plist = services.getAll();
        Assert.assertEquals(n,plist.size());
    }
    @Test
    public void get() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();
        List<ProductPojo> productPojoList = dao.selectAll();

        int index  = productPojoList.get(n-1).getId();
        ProductData p = services.get(index);
        Assert.assertEquals(index,p.getId());
        try {
            services.get(index+1);
        }catch (ApiException e){
            Assert.assertEquals("Product with given id does not exist, id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void addEmptyObject() throws ApiException {
        ProductForm p = new ProductForm();
        try {
            services.add(p);
        }catch (Exception e){
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
            services.add(p);
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

        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(pojo.getBarcode());
        p.setName(name);
        p.setMrp(mrp);
        try{
            services.add(p);
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
        bDao.insert(pb);

        ProductForm p = new ProductForm();

        String name = getRandomString();
        double mrp = Math.random()*100%(20);


        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(getRandomString() + " #");
        p.setName(name);
        p.setMrp(mrp);
        try{
            services.add(p);
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
        bDao.insert(pb);

        ProductForm p = new ProductForm();

        String name = getRandomString();
        double mrp = Math.random()*100%(20);


        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(getRandomString());
        p.setName(name);
        p.setMrp(mrp);

        services.add(p);

    }

    @Test
    public void bulkAddEmptyObject(){

        ProductForm p = new ProductForm();
        List<ProductForm> pList = new ArrayList<>();
        pList.add(p);

        try{
            services.bulkAdd(pList);
        } catch (ApiException e) {
            String err = "Error : row -> " + 1 + " parameters in the Insert form cannot be null<br>";
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
        bDao.insert(pb);
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
            services.bulkAdd(pList);
        }catch (ApiException e){
            String err = "Error : row -> " + 1 + " barcode "  + p.getBarcode() +  " already exists<br>";
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
            services.bulkAdd(pList);
        }catch (ApiException e){
            String err = "Error : row -> " + 1 + " " + p.getBrand() + " - " + p.getCategory() + " brand-category does not exist<br>";
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
        bDao.insert(pb);

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
            services.bulkAdd(pList);
        }catch (ApiException e){
            String err = "Error : row -> " + 1 + " barcode "  + p.getBarcode() +  " not valid, barcode can only have alphanumeric values<br>";
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
        bDao.insert(pb);

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

        services.bulkAdd(pList);


    }
    @Test
    public void update() throws ApiException {
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.insert(pb);


        ProductPojo p = daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(p.getBarcode());
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        services.update(pUpdate);

    }
    @Test
    public void updateEmptyBodyError() throws ApiException {

        ProductUpdateForm pUpdate = new ProductUpdateForm();
        try{
        services.update(pUpdate);
        }catch (ApiException e){
            String err = "parameters in the Update form cannot be null";
            Assert.assertEquals(err,e.getMessage());
        }

    }
    @Test
    public void updateBarcodeUniqueError() throws ApiException {

        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        ProductPojo p = daoInsertHelper();
        ProductPojo p1= daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(p.getBrand());
        pUpdate.setCategory(p.getCategory());
        pUpdate.setBarcode(p1.getBarcode());
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        try{
        services.update(pUpdate);
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
        bDao.insert(pb);


        ProductPojo p = daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(p.getBarcode() + "# ");
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        try{
            services.update(pUpdate);
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
        bDao.insert(pb);
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
            services.update(pUpdate);
        }catch (ApiException e){
            String err = "product with given id does not exist, id : " + id;
            Assert.assertEquals(err,e.getMessage());
        }

    }
    @Test
    public void updateBrandError() throws ApiException {
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
            services.update(pUpdate);
        }catch (ApiException e){
            String err = pUpdate.getBrand() + " - " + pUpdate.getCategory() + " brand-category does not exist";
            Assert.assertEquals(err,e.getMessage());
        }

    }

    @Test
    public void checkIfBrandExist(){
        Assert.assertTrue(!services.checkIfBrandExist(0));
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase(Locale.ROOT);

        //add brand category
        BrandPojo pb = new BrandPojo();
        pb.setBrand(brand);
        pb.setCategory(category);
        bDao.insert(pb);

        ProductPojo p = daoInsertHelper();
        ProductUpdateForm pUpdate = new ProductUpdateForm();
        pUpdate.setName(getRandomString());
        pUpdate.setBrand(brand);
        pUpdate.setCategory(category);
        pUpdate.setBarcode(p.getBarcode());
        pUpdate.setMrp(p.getMrp());
        pUpdate.setId(p.getId());

        Assert.assertTrue(services.checkIfBrandExist(dao.selectAll().get(0).getBrandPojoId()));


    }
    public ProductPojo daoInsertHelper(){
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
        p.setBrandPojoId(brandPojoId);
        p.setMrp(mrp);
        dao.insert(p);
        return p;
    }

}
