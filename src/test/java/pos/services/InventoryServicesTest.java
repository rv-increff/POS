package pos.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.InventoryDao;
import pos.dao.OrderItemDao;
import pos.dao.ProductDao;
import pos.model.*;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.ProductPojo;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


import static pos.util.RandomUtil.getRandomNumber;
import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class InventoryServicesTest {
    @Resource
    private InventoryServices services;
    @Resource
    private InventoryDao dao;
    @Resource
    private ProductDao pDao;
    @Resource
    private OrderItemDao oIDao;

    @Test
    public void getAll() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();

        List<InventoryData> plist = services.getAll();
        Assert.assertEquals(n,plist.size());
    }
    @Test
    public void get() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();
        List<InventoryPojo> pList = dao.selectAll();

        int index  = pList.get(n-1).getId();
        InventoryData p = services.get(index);
        Assert.assertEquals(index,p.getId());
        try {
            services.get(index+1);
        }catch (ApiException e){
            Assert.assertEquals("Inventory with given id does not exist, id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void addEmptyObject() {
        InventoryInsertForm p = new InventoryInsertForm();
        try {
            services.add(p);
        }catch (Exception e){
            Assert.assertEquals("Barcode or quantity cannot be NULL",e.getMessage());
        }
    }

    @Test
    public void addInventoryExistError(){

        InventoryPojo in = daoInsertHelper();
        InventoryInsertForm p = new InventoryInsertForm();

        p.setBarcode(in.getBarcode());
        p.setQuantity(in.getQuantity());

        try{
            services.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Inventory data already exist update the record instead",e.getMessage());
        }
    }

    @Test
    public void addProductBarcodeError() {
        InventoryInsertForm p = new InventoryInsertForm();

        p.setBarcode(getRandomString());
        p.setQuantity(1000000000);

        try{
            services.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Product with this barcode does not exist",e.getMessage());
        }
    }

    @Test
    public void add() throws ApiException {
        ProductPojo pj = pDaoInsertHelper();

        InventoryInsertForm p = new InventoryInsertForm();

        p.setBarcode(pj.getBarcode());
        p.setQuantity(1000000000);

        services.add(p);
    }

    @Test
    public void bulkAddEmptyObject() {
        InventoryInsertForm p = new InventoryInsertForm();
        List<InventoryInsertForm> pList = new ArrayList<>();
        pList.add(p);
        try {
            services.bulkAdd(pList);
        }catch (Exception e){
            Assert.assertEquals("Error : row -> " + 1 + " barcode or quantity cannot be NULL<br>",e.getMessage());
        }
    }

    @Test
    public void bulkAddBarcodeError() {
        InventoryInsertForm p = new InventoryInsertForm();

        p.setBarcode(getRandomString());
        p.setQuantity(10000);

        List<InventoryInsertForm> pList = new ArrayList<>();
        pList.add(p);
        try {
            services.bulkAdd(pList);
        }catch (Exception e){
            Assert.assertEquals("Error : row -> " + 1 + " product with the barcode " + p.getBarcode() + " does not exist<br>",e.getMessage());
        }
    }

    @Test
    public void bulkAddInventoryExistError() throws ApiException {
        ProductPojo pj = pDaoInsertHelper();
        InventoryPojo in = daoInsertHelper();
        InventoryInsertForm p = new InventoryInsertForm();

        p.setBarcode(pj.getBarcode());
        p.setQuantity(in.getQuantity());
        List<InventoryInsertForm> pList = new ArrayList<>();
        pList.add(p);
        services.bulkAdd(pList);
        try {
            services.bulkAdd(pList);
        }catch (ApiException e){
            Assert.assertEquals("Error : row -> " + 1 + " Inventory data already exist for barcode "+ p.getBarcode() +" update the record instead<br>",e.getMessage());
        }
    }

    @Test
    public void delete() throws ApiException {
        InventoryPojo p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();
        services.delete(id);
        try{
            services.delete(id);
        }catch (ApiException e){
            String err = "Inventory with given id does not exist, id : " + id;
            Assert.assertEquals(err,e.getMessage());
        }
    }
    @Test
    public void deleteProductItemError() throws ApiException {
        InventoryPojo p = daoInsertHelper();
        daoOrderItemInsertHelper(p.getProductId());
        int id = dao.selectAll().get(0).getId();
        try{
            services.delete(id);
        }catch (ApiException e){
            String err = "OrderItem exist for the product in the inventory cannot delete inventory Item, id : " + id;
            Assert.assertEquals(err,e.getMessage());
        }
    }

    @Test
    public void update() throws ApiException {
        InventoryPojo p = daoInsertHelper();
        InventoryUpdateForm inv = new InventoryUpdateForm();
        inv.setId(p.getId());
        inv.setQuantity(p.getQuantity()+100);
        services.update(inv);
    }

    @Test
    public void updateNotExistError() {
//        InventoryPojo p = daoInsertHelper();
        InventoryUpdateForm inv = new InventoryUpdateForm();
        int id = 2;
        inv.setId(id);
        inv.setQuantity(100);
        try{
        services.update(inv);
        }catch (ApiException e){
            Assert.assertEquals("Inventory with given id does not exist, id : " + id,e.getMessage());
        }
    }

    @Test
    public void updateEmptyObjectError() {
        InventoryUpdateForm inv = new InventoryUpdateForm();

        try{
        services.update(inv);
        }catch (ApiException e){
            Assert.assertEquals("Quantity cannot be NULL", e.getMessage());
        }
    }
    private InventoryPojo daoInsertHelper(){
        InventoryPojo p = new InventoryPojo();
        String barcode = getRandomString();
        int productId = (int) (Math.random()*1000%(20));
        int qty = (int)Math.random()*10000%(20);
        p.setBarcode(barcode);
        p.setProductId(productId);
        p.setQuantity(qty+1);
        dao.insert(p);
        return p;
    }

    public ProductPojo pDaoInsertHelper(){
        ProductPojo p = new ProductPojo();
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase();
        String name = getRandomString().toLowerCase();
        String barcode = getRandomString();
        int brandPojoId = (int) (Math.random()*1000);
        double mrp = Math.random()*1000;
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(barcode);
        p.setName(name);
        p.setBrandPojoId(brandPojoId);
        p.setMrp(mrp+1);
        pDao.insert(p);
        return p;
    }
    private OrderItemPojo daoOrderItemInsertHelper(int productId){
        OrderItemPojo p = new OrderItemPojo();
        p.setSellingPrice(getRandomNumber()+1);
        p.setOrderId(getRandomNumber());
        p.setQuantity(getRandomNumber()+1);
        p.setProductId(productId);
        oIDao.insert(p);
        return p;
    }
}