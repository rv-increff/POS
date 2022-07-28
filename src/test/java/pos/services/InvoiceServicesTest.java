package pos.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.BrandDao;
import pos.dao.OrderDao;
import pos.dao.OrderItemDao;
import pos.dao.ProductDao;
import pos.model.SalesReport;
import pos.model.SalesReportForm;
import pos.pojo.BrandPojo;
import pos.pojo.OrderItemPojo;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;
import pos.spring.ApiException;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static pos.util.RandomUtil.getRandomNumber;
import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class InvoiceServicesTest {

    @Resource
    InvoiceServices service;

    @Resource
    OrderItemDao dao;
    @Resource
    OrderDao oDao;
    @Resource
    ProductDao pDao;
    @Resource
    BrandDao bDao;

    @Test
    public void getSalesReport() throws ParseException, ApiException {
        String brand = getRandomString();
        String category  = getRandomString();

        //add brand
        BrandPojo bp = new BrandPojo();
        bp.setBrand(brand);
        bp.setCategory(category);
        bDao.add(bp);

        ProductPojo pp = new ProductPojo();
        pp.setCategory(category);
        pp.setBrand(brand);
        pp.setMrp((double)getRandomNumber());
        pp.setName(getRandomString());
        pp.setBarcode(getRandomString());
        pp.setBrandId(getRandomNumber());
        pDao.insert(pp);

        //make order
        Date date = new Date();
        OrderPojo op = new OrderPojo();
        op.setTime(date);
        op.setOrderPlaced(true);
        oDao.add(op);

        //orderItem
        int productId = pDao.selectAll().get(0).getId();
        int orderId = oDao.selectAll().get(0).getId();
        OrderItemPojo oi = new OrderItemPojo();
        oi.setOrderId(orderId);
        oi.setProductId(productId);
        oi.setQuantity(getRandomNumber());
        oi.setSellingPrice((double)getRandomNumber());
        dao.add(oi);


        LocalDate now = LocalDate.now();
        DateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String to  = now.plusYears(1).toString();
        String from = now.minusYears(1).toString();

        SalesReportForm sf = new SalesReportForm();
        sf.setFrom("");
        sf.setTo("");
        sf.setBrand("");
        sf.setCategory("");
        SalesReport s = dao.getSalesReport(sf).get(0);
        Assert.assertEquals(1,dao.getSalesReport(sf).size());
        sf.setFrom(from);
        sf.setTo(to);
        sf.setBrand(brand);
        sf.setCategory(category);

        Assert.assertEquals(1,service.getSalesReport(sf).size());
    }

}
