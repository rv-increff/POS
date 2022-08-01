package pos.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dto.QaConfig;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static pos.util.RandomUtil.getRandomNumber;
import static pos.util.RandomUtil.getRandomString;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class OrderItemDaoTest {
    @Resource
    OrderItemDao dao;
    @Resource
    OrderDao oDao;
    @Resource
    ProductDao pDao;

    @Test
    public void daoInsert(){
        OrderItemPojo p = new OrderItemPojo();
        p.setProductId(getRandomNumber());
        p.setOrderId(getRandomNumber());
        p.setSellingPrice((double)getRandomNumber());
        p.setQuantity(getRandomNumber());

        dao.add(p);
    }
    @Test
    public void daoSelectAll(){
        int n = 5;
        for(int i=0;i<n;i++) {
            daoInsertHelper();
        }
        List<OrderItemPojo> plist = dao.selectAll();
        Assert.assertEquals(n,plist.size());

    }

    @Test
    public void daoSelect(){
        daoInsertHelper();
        int id = dao.selectAll().get(0).getId();
        Assert.assertNotNull(dao.select(id));
        Assert.assertNull(dao.select(id+1));
    }

    @Test
    public void daoDelete(){
        daoInsertHelper();
        daoInsertHelper();
        int id = dao.selectAll().get(0).getId();
        int id2 = dao.selectAll().get(1).getId();
        dao.delete(id);
        Assert.assertNotNull(dao.select(id2));
        Assert.assertNull(dao.select(id));
    }

    @Test
    public void daoSelectFromOrderId(){
        Assert.assertEquals(0,dao.selectFromOrderId(getRandomNumber()).size());
        int n = 5;
        int orderID = getRandomNumber();
        for(int i=0;i<n;i++){
            OrderItemPojo p = new OrderItemPojo();
            int productId = i+1;
            p.setProductId(productId);
            p.setOrderId(orderID);
            p.setSellingPrice((double)getRandomNumber());
            p.setQuantity(getRandomNumber());
            dao.add(p);
        }
        Assert.assertEquals(n,dao.selectFromOrderId(orderID).size());

    }

    @Test
    public void daoUpdate(){
        dao.update();
    }

    @Test
    public void daoCheckOrderExist(){
        Assert.assertNull(dao.selectFromOrderIdProductId(getRandomNumber(),getRandomNumber()));

        OrderPojo o = daoOrderInsertHelper();

        int productId = getRandomNumber();
        OrderItemPojo p = new OrderItemPojo();
        p.setProductId(productId);
        p.setOrderId(o.getId());
        p.setSellingPrice((double)getRandomNumber());
        p.setQuantity(getRandomNumber());
        dao.add(p);

        Assert.assertNotNull(dao.selectFromOrderIdProductId(o.getId(),productId));
    }

//    @Test
    public void daoGetSalesReport() throws ParseException {
        //make product
//        String brand = getRandomString();
//        String category  = getRandomString();
//        ProductPojo pp = new ProductPojo();
//        pp.setCategory(category);
//        pp.setBrand(brand);
//        pp.setMrp((double)getRandomNumber());
//        pp.setName(getRandomString());
//        pp.setBarcode(getRandomString());
//        pp.setBrandId(getRandomNumber());
//        pDao.insert(pp);
//
//        //make order
//        ZonedDateTime date = ZonedDateTime.now(ZoneId.systemDefault());
//        OrderPojo op = new OrderPojo();
//        op.setOrderPlaced(true);
//        op.setTime(date);
//        oDao.add(op);
//
//        //orderItem
//        int productId = pDao.selectAll().get(0).getId();
//        int orderId = oDao.selectAll().get(0).getId();
//        OrderItemPojo oi = new OrderItemPojo();
//        oi.setOrderId(orderId);
//        oi.setProductId(productId);
//        oi.setQuantity(getRandomNumber());
//        oi.setSellingPrice((double)getRandomNumber());
//        dao.add(oi);
//
//
//        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
//        DateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
//        DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//        String to  = now.plusYears(1).toString();
//        String from = now.minusYears(1).toString();
//
//        SalesReportForm sf = new SalesReportForm();
//        sf.setFrom("");
//        sf.setTo("");
//        sf.setBrand("");
//        sf.setCategory("");
//        SalesReport s = dao.getSalesReport(sf).get(0);
//        Assert.assertEquals(1,dao.getSalesReport(sf).size());
//        sf.setFrom(from);
//        sf.setTo(to);
//        sf.setBrand(brand);
//        sf.setCategory(category);
//
//        Assert.assertEquals(1,dao.getSalesReport(sf).size());

    }
    @Test
    public void getOrderItemsForProductId(){
        int n = 5;
        int productId = getRandomNumber();
        for(int i=0;i<n;i++){
            daoInsertHelperProductId(productId);
        }
        Assert.assertNotNull(dao.selectFromProductId(productId));
    }

    private OrderItemPojo daoInsertHelper(){
        OrderItemPojo p = new OrderItemPojo();
        p.setProductId(getRandomNumber());
        p.setOrderId(getRandomNumber());
        p.setSellingPrice((double)getRandomNumber());
        p.setQuantity(getRandomNumber());

        dao.add(p);
        return p;
    }
    private OrderItemPojo daoInsertHelperProductId(int productId){
        OrderItemPojo p = new OrderItemPojo();
        p.setProductId(productId);
        p.setOrderId(getRandomNumber());
        p.setSellingPrice((double)getRandomNumber());
        p.setQuantity(getRandomNumber());

        dao.add(p);
        return p;
    }
    private OrderPojo daoOrderInsertHelper(){
        OrderPojo p = new OrderPojo();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        p.setTime(now);
        oDao.add(p);
        return p;
    }

}
