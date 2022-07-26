package pos.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.InventoryDao;
import pos.dao.OrderDao;
import pos.dao.OrderItemDao;
import pos.dao.ProductDao;
import pos.model.OrderItemData;
import pos.model.OrderItemInsertForm;
import pos.model.OrderItemUpdateForm;
import pos.pojo.InventoryPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static pos.util.RandomUtil.getRandomNumber;
import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class OrderItemServiceTest {

    @Resource
    private OrderItemServices services;
    @Resource
    private OrderItemDao dao;
    @Resource
    private OrderDao oDao;
    @Resource
    private ProductDao pDao;
    @Resource
    private InventoryDao iDao;

    @Test
    public void getAll() throws ApiException {
        for(int i=0;i<5;i++)daoInsertHelper();

        List<OrderItemData> plist = services.getAll();
        Assert.assertEquals(5,plist.size());
    }

    @Test
    public void get() throws ApiException {
        for(int i=0;i<5;i++)daoInsertHelper();
        List<OrderItemPojo> orderItemPojoList = dao.selectAll();
        int index  = orderItemPojoList.get(4).getId();
        OrderItemData p = services.get(index);
        Assert.assertEquals(index,p.getId());
        try {
            services.get(index+1);
        }catch (Exception e){
            Assert.assertEquals("OrderItem with given id does not exist ,id : " + (index+1),e.getMessage());
        }
    }
    @Test
    public void addOrderItemExistError() {
        OrderItemPojo oi = daoInsertHelper();
        OrderItemInsertForm p = new OrderItemInsertForm();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(oi.getOrderId());
        p.setQuantity(getRandomNumber());
        p.setProductId(oi.getProductId());
        try {
        services.add(p);
        }catch (ApiException e){
            Assert.assertEquals("OrderItem already exist update that instead",e.getMessage());
        }
    }

    @Test
    public void addOrderNotExistError() {
        OrderItemInsertForm p = new OrderItemInsertForm();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(getRandomNumber());
        p.setQuantity(getRandomNumber());
        p.setProductId(getRandomNumber());
        try {
            services.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Order with this id does not exist, id : " + p.getOrderId(),e.getMessage());
        }
    }
    @Test
    public void addProductNotExistError() {
        OrderPojo op = daoOrderInsertHelper();
        int orderId = oDao.selectAll().get(0).getId();
        OrderItemInsertForm p = new OrderItemInsertForm();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(orderId);
        p.setQuantity(getRandomNumber());
        p.setProductId(getRandomNumber());
        try {
            services.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Product with this id does not exist, id : " + p.getProductId(),e.getMessage());
        }
    }
    @Test
    public void addInventoryNotExistError() {
        daoOrderInsertHelper();
        daoProductInsertHelper();

        int orderId = oDao.selectAll().get(0).getId();
        int productId = pDao.selectAll().get(0).getId();
        OrderItemInsertForm p = new OrderItemInsertForm();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(orderId);
        p.setQuantity(getRandomNumber());
        p.setProductId(productId);
        try {
            services.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Product with this id does not exist in the inventory, id : " + p.getProductId(),e.getMessage());
        }
    }

    @Test
    public void addQtyError() throws ApiException {
        daoOrderInsertHelper();
        daoProductInsertHelper();

        int orderId = oDao.selectAll().get(0).getId();
        int productId = pDao.selectAll().get(0).getId();
        int qty = getRandomNumber();
        daoInventoryInsertHelper(productId,qty);

        OrderItemInsertForm p = new OrderItemInsertForm();
        p.setSellingPrice(getRandomNumber() + 5);
        p.setOrderId(orderId);
        p.setQuantity(qty+2);
        p.setProductId(productId);
        try {
            services.add(p);
        }catch (ApiException e){
            Assert.assertEquals("Selected quantity more than available quantity, available quantity only " + qty ,e.getMessage());
        }
        p = new OrderItemInsertForm();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(orderId);
        p.setQuantity(qty-2);
        p.setProductId(productId);
        services.add(p);
    }

    @Test
    public void getOrderItemForOrder() throws ApiException {
        daoOrderInsertHelper();
        int orderId = oDao.selectAll().get(0).getId();
        int n = 5;
        for(int i=0;i<n;i++){
            daoInsertHelper(orderId);
        }
        List<OrderItemData> oList = services.getOrderItemForOrder(orderId);
        Assert.assertEquals(n,oList.size());

        try{
        services.getOrderItemForOrder(orderId+1);
        }catch (ApiException e){
        Assert.assertEquals("Order with this id does not exist, id : " + (orderId+1),e.getMessage());
        }
    }

    @Test
    public void delete() throws ApiException {
        OrderPojo op = daoOrderInsertHelper();
        daoProductInsertHelper();
        int productId = pDao.selectAll().get(0).getId();
        daoInventoryInsertHelper(productId,getRandomNumber());
        System.out.println(iDao.selectAll().get(0).getProductId() + "---" + productId);

        OrderItemPojo p = new OrderItemPojo();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(op.getId());
        p.setQuantity(getRandomNumber());
        p.setProductId(productId);
        dao.insert(p);

        int id = dao.selectAll().get(0).getId();
//        services.get(id);
        System.out.println(id + "_----" + dao.selectAll().size());
        services.delete(id);
    }

    @Test
    public void deleteError() throws ApiException {
        OrderPojo op = daoOrderInsertHelper();
        daoProductInsertHelper();
        int productId = pDao.selectAll().get(0).getId();
        daoInventoryInsertHelper(productId,getRandomNumber());

        OrderItemPojo p = daoInsertHelper(op.getId());
        int id = dao.selectAll().get(0).getId();
        try{
        services.delete(id);
        }catch (ApiException e){
            Assert.assertEquals("Product with given product id does not exist in the inventory, product id : " + p.getProductId(),e.getMessage());
        }
    }

    @Test
    public void updateIdError(){
//        OrderItemPojo p = daoInsertHelper();
        OrderItemUpdateForm up = new OrderItemUpdateForm();
        up.setId(1);
        up.setQuantity(getRandomNumber());
        up.setSellingPrice(getRandomNumber());
        try{
            services.update(up);
        }catch(ApiException e){
            Assert.assertEquals("OrderItem with given id does not exist ,id : " + 1,e.getMessage());
        }

    }
    @Test
    public void updateProductNotInInvError(){
        OrderPojo op = daoOrderInsertHelper();
        OrderItemPojo p = daoInsertHelper(op.getId());
        OrderItemUpdateForm up = new OrderItemUpdateForm();
        up.setId(p.getId());
        up.setQuantity(p.getQuantity() + 5);
        up.setSellingPrice(getRandomNumber());
        try{
            services.update(up);
        }catch(ApiException e){
            Assert.assertEquals("Product with given product id does not exist in the inventory, product id : " + p.getProductId(),e.getMessage());
        }
    }

    @Test
    public void updateQtyError() {
        OrderPojo op = daoOrderInsertHelper();
        OrderItemPojo p = daoInsertHelper(op.getId());
        OrderItemUpdateForm up = new OrderItemUpdateForm();
        //add in inv
        InventoryPojo invP = daoInventoryInsertHelper(p.getProductId(),p.getQuantity());

        up.setId(p.getId());
        up.setQuantity(invP.getQuantity()+ p.getQuantity() + 5);
        up.setSellingPrice(getRandomNumber());

        try{
            services.update(up);
        }catch(ApiException e){
            Assert.assertEquals("Selected quantity more than available quantity, available quantity only " + invP.getQuantity(),e.getMessage() );
        }

    }
    @Test
    public void update() throws ApiException {
        OrderPojo op = daoOrderInsertHelper();
        OrderItemPojo p = daoInsertHelper(op.getId());
        OrderItemUpdateForm up = new OrderItemUpdateForm();
        up.setId(p.getId());
        up.setQuantity(p.getQuantity() + 5);
        up.setSellingPrice(getRandomNumber());

        //add in inv
        daoInventoryInsertHelper(p.getProductId(),p.getQuantity());

        services.update(up);

    }
    @Test
    public void checkOrderItemWithProductId(){
        OrderItemPojo p = daoInsertHelper();

       Assert.assertTrue(!services.checkOrderItemWithProductId(p.getProductId()));
    }
    private OrderItemPojo daoInsertHelper(){
        OrderItemPojo p = new OrderItemPojo();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(getRandomNumber());
        p.setQuantity(getRandomNumber());
        p.setProductId(getRandomNumber());
        dao.insert(p);
        return p;
    }
    private OrderItemPojo daoInsertHelper(int orderId){
        OrderItemPojo p = new OrderItemPojo();
        p.setSellingPrice(getRandomNumber());
        p.setOrderId(orderId);
        p.setQuantity(getRandomNumber());
        p.setProductId(getRandomNumber());
        dao.insert(p);
        return p;
    }
    private OrderPojo daoOrderInsertHelper(){
        OrderPojo p = new OrderPojo();
        Date now = new Date();
        p.setTime(now);
        oDao.insert(p);
        return p;
    }
    private ProductPojo daoProductInsertHelper(){
        ProductPojo p = new ProductPojo();
        String category = getRandomString();
        String brand = getRandomString();
        String name = getRandomString();
        String barcode = getRandomString();
        int brandPojoId = (int) (Math.random()*100%(20));
        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(barcode);
        p.setName(name);
        p.setBrandPojoId(brandPojoId);
        p.setMrp(mrp);
        pDao.insert(p);
        return p;
    }
    private InventoryPojo daoInventoryInsertHelper(int productId,int qty){
        InventoryPojo p = new InventoryPojo();
        p.setProductId(productId);
        p.setQuantity(qty);
        p.setBarcode(getRandomString());
        iDao.insert(p);
        return p;
    }
}
