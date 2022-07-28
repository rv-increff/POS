package pos.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.pojo.OrderPojo;
import pos.dto.QaConfig;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class OrderDaoTest {
    @Resource
    private OrderDao dao;

    @Test
    public void daoInsert(){
        OrderPojo p = new OrderPojo();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        p.setTime(now);
        dao.add(p);
    }

    @Test
    public void daoSelectAll(){
        int n = 5;
        for(int i=0;i<n;i++) {
            daoInsertHelper();
        }
        List<OrderPojo> plist = dao.selectAll();
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
    public void daoCheckOrderId(){
        daoInsertHelper();
        Integer id = dao.selectAll().get(0).getId();
        Assert.assertEquals(id,dao.select(id).getId());
        Assert.assertNull(dao.select(id+1));
    }

    private OrderPojo daoInsertHelper(){
        OrderPojo p = new OrderPojo();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        p.setTime(now);
        dao.add(p);
        return p;
    }

}
