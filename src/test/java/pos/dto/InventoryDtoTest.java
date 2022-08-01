package pos.dto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.InventoryDao;
import pos.dao.OrderItemDao;
import pos.dao.ProductDao;
import pos.model.InventoryData;
import pos.model.InventoryForm;
import pos.model.InventoryUpdateForm;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static pos.util.RandomUtil.getRandomNumber;
import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class InventoryDtoTest {
    @Autowired
    private InventoryDto dto;
    @Autowired
    private InventoryDao dao;
    @Autowired
    private ProductDao pDao;
    @Autowired
    private OrderItemDao oIDao;

    @Test
    public void getAll() throws ApiException {
        Integer n =5;
        for(Integer i=0;i<n;i++)daoInsertHelper();

        List<InventoryData> plist = dto.getAll();
        Assert.assertEquals(n,(Integer)plist.size());
    }
    @Test
    public void get() throws ApiException {
        Integer n =5;
        for(Integer i=0;i<n;i++)daoInsertHelper();
        List<InventoryPojo> pList = dao.selectAll();

        Integer index  = pList.get(n-1).getId();
        InventoryData p = dto.get(index);
        Assert.assertEquals(index,p.getId());
        try {
            dto.get(index+1);
        }catch (ApiException e){
            Assert.assertEquals("Inventory with given id does not exist, id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void addEmptyObject() {
        InventoryForm p = new InventoryForm();
        try {
            dto.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Barcode or quantity cannot be NULL",e.getMessage());
        }
    }

    @Test
    public void addInventoryExistError(){

        InventoryPojo in = daoInsertHelper();
        InventoryForm p = new InventoryForm();

        p.setBarcode(in.getBarcode());
        p.setQuantity(in.getQuantity());

        try{
            dto.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Inventory data already exist update the record instead",e.getMessage());
        }
    }

    @Test
    public void addProductBarcodeError() {
        InventoryForm p = new InventoryForm();

        p.setBarcode(getRandomString());
        p.setQuantity(1000000000);

        try{
            dto.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Product with this barcode does not exist",e.getMessage());
        }
    }

    @Test
    public void add() throws ApiException {
        ProductPojo pj = pDaoInsertHelper();

        InventoryForm p = new InventoryForm();

        p.setBarcode(pj.getBarcode());
        p.setQuantity(1000000000);

        dto.add(p);
    }

    @Test
    public void bulkAddEmptyObject() {
        InventoryForm p = new InventoryForm();
        List<InventoryForm> pList = new ArrayList<>();
        pList.add(p);
        try {
            dto.bulkAdd(pList);
        }catch (ApiException e){
            Assert.assertEquals("Error : row -> " + 1 + " barcode or quantity cannot be NULL\n",e.getMessage());
        }
    }

    @Test
    public void bulkAddBarcodeError() {
        InventoryForm p = new InventoryForm();

        p.setBarcode(getRandomString());
        p.setQuantity(10000);

        List<InventoryForm> pList = new ArrayList<>();
        pList.add(p);
        try {
            dto.bulkAdd(pList);
        }catch (ApiException e){
            Assert.assertEquals("Error : row -> " + 1 + " product with the barcode " + p.getBarcode() + " does not exist\n",e.getMessage());
        }
    }

    @Test
    public void bulkAddInventoryExistError() throws ApiException {
        ProductPojo pj = pDaoInsertHelper();
        InventoryPojo in = daoInsertHelper();
        InventoryForm p = new InventoryForm();

        p.setBarcode(pj.getBarcode());
        p.setQuantity(in.getQuantity());
        List<InventoryForm> pList = new ArrayList<>();
        pList.add(p);
        dto.bulkAdd(pList);
        try {
            dto.bulkAdd(pList);
        }catch (ApiException e){
            Assert.assertEquals("Error : row -> " + 1 + " Inventory data already exist for barcode "+ p.getBarcode() +" update the record instead\n",e.getMessage());
        }
    }

    @Test
    public void update() throws ApiException {
        InventoryPojo p = daoInsertHelper();
        InventoryUpdateForm inv = new InventoryUpdateForm();
        inv.setId(p.getId());
        inv.setQuantity(p.getQuantity()+100);
        dto.update(inv);
    }

    @Test
    public void updateNotExistError() {
//        InventoryPojo p = daoInsertHelper();
        InventoryUpdateForm inv = new InventoryUpdateForm();
        Integer id = 2;
        inv.setId(id);
        inv.setQuantity(100);
        try{
        dto.update(inv);
        }catch (ApiException e){
            Assert.assertEquals("Inventory with given id does not exist, id : " + id,e.getMessage());
        }
    }

    @Test
    public void updateEmptyObjectError() {
        InventoryUpdateForm inv = new InventoryUpdateForm();
        try{
        dto.update(inv);
        }catch (ApiException e){
            Assert.assertEquals("Quantity cannot be NULL", e.getMessage());
        }
    }
    private InventoryPojo daoInsertHelper(){
        InventoryPojo p = new InventoryPojo();
        String barcode = getRandomString();
        Integer productId = (int) (Math.random()*1000%(20));
        Integer qty = (int)Math.random()*10000%(20);
        p.setBarcode(barcode);
        p.setProductId(productId);
        p.setQuantity(qty+1);
        dao.add(p);
        return p;
    }

    public ProductPojo pDaoInsertHelper(){
        ProductPojo p = new ProductPojo();
        String category = getRandomString().toLowerCase();
        String brand = getRandomString().toLowerCase();
        String name = getRandomString().toLowerCase();
        String barcode = getRandomString();
        Integer brandPojoId = (int) (Math.random()*1000);
        double mrp = Math.random()*1000;
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(barcode);
        p.setName(name);
        p.setBrandId(brandPojoId);
        p.setMrp(mrp+1);
        pDao.add(p);
        return p;
    }
    private OrderItemPojo daoOrderItemInsertHelper(Integer productId){
        OrderItemPojo p = new OrderItemPojo();
        p.setSellingPrice((double)getRandomNumber()+1);
        p.setOrderId(getRandomNumber());
        p.setQuantity(getRandomNumber()+1);
        p.setProductId(productId);
        oIDao.add(p);
        return p;
    }
}
