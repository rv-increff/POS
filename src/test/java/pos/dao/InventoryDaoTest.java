package pos.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dto.QaConfig;
import pos.pojo.InventoryPojo;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class InventoryDaoTest {

    @Resource
    InventoryDao dao;
    @Resource
    ProductDao pDao;

    @Test
    public void daoInsert(){
        InventoryPojo p = new InventoryPojo();
        int productId = (int) (Math.random()*100%(20));
        int qty = (int) (Math.random()*100%(20));
        p.setProductId(productId);
        p.setQuantity(qty);
        p.setBarcode(getRandomString());
        dao.add(p);
    }

    @Test
    public void daoSelectAll(){
        int n = 5;
        for(int i=0;i<n;i++) {
            daoInsertHelper();
        }
        List<InventoryPojo> plist = dao.selectAll();
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
    public void daoUnique(){
        InventoryPojo p = daoInsertHelper();
        Assert.assertNull(dao.selectByBarcode(getRandomString()));
        Assert.assertNotNull(dao.selectByBarcode(p.getBarcode()));
    }

    @Test
    public void daoGetIdFromBarcode(){

//        ProductPojo pp = pDaoInsertHelper();

        InventoryPojo p = daoInsertHelper();
        Integer id = dao.selectAll().get(0).getId();
        Assert.assertEquals(id,dao.selectByProductId(p.getProductId()).getId());
        Assert.assertNull(dao.selectByProductId(p.getProductId()+1));
    }

    @Test
    public void daoGetInventoryReport(){
        dao.getInventoryReport();
    }
    @Test
    public void daoUpdate(){
        dao.update();
    }
    private InventoryPojo daoInsertHelper(){
        InventoryPojo p = new InventoryPojo();
        int productId = (int) (Math.random()*1000);
        int qty = (int) (Math.random()*1000);
        p.setProductId(productId);
        p.setQuantity(qty+1);
        p.setBarcode(getRandomString());
        dao.add(p);
        return p;
    }



}
