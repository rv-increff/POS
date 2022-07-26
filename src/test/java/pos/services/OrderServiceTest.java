package pos.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.OrderDao;
import pos.dao.ProductDao;
import pos.model.OrderData;
import pos.model.ProductData;
import pos.pojo.OrderPojo;
import pos.pojo.ProductPojo;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class OrderServiceTest {

    @Resource
    private OrderServices services;
    @Resource
    private OrderDao dao;


    @Test
    public void getAll() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();

        List<OrderData> plist = services.getAll();
        Assert.assertEquals(n,plist.size());
    }
    @Test
    public void get() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();
        List<OrderPojo> orderPojoList = dao.selectAll();

        int index  = orderPojoList.get(n-1).getId();
        OrderData p = services.get(index);
        Assert.assertEquals(index,p.getId());
        try {
            services.get(index+1);
        }catch (ApiException e){
            Assert.assertEquals("Order with given id does not exist, id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void add() throws ApiException {
        services.add();
    }

    private OrderPojo daoInsertHelper(){
        OrderPojo p = new OrderPojo();
        Date now = new Date();
        p.setTime(now);
        dao.insert(p);
        return p;
    }
}

