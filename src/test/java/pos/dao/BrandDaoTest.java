package pos.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.pojo.BrandPojo;
import pos.services.QaConfig;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.*;
import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class BrandDaoTest {

    @Resource
    private BrandDao dao;

    @Test
    public void daoInsert(){
        BrandPojo p = new BrandPojo();
        String category = getRandomString();
        String brand = getRandomString();
        p.setCategory(category);
        p.setBrand(brand);
        dao.insert(p);
    }

    @Test
    public void daoSelectAll(){
        for(int i=0;i<5;i++) {
            daoInsert();
        }
        List<BrandPojo> p = dao.selectAll();
        assertSame(5,p.size());
    }
    @Test
    public void daoSelect(){
        BrandPojo pExpected = dao.select(1);
        assertNull(pExpected);

        int index=1;
        daoInsertHelper();
        for(BrandPojo i : dao.selectAll()){
            index = i.getId();
        }
        pExpected = dao.select(index);
        assertNotNull(pExpected);
    }
    @Test
    public void daoDelete(){
        daoInsertHelper();
        BrandPojo p = daoInsertHelper();
        int index=dao.selectAll().get(1).getId();
        dao.delete(index);
        assertNull(dao.select(index));
        assertNotNull(dao.select(index-1));
    }

    @Test
    public void daoUnique(){
        String brand = getRandomString();
        String category = getRandomString();

        Assert.assertTrue(dao.unique(brand, category));

        BrandPojo p = daoInsertHelper();

        Assert.assertTrue(!dao.unique(p.getBrand(), p.getCategory()));


    }

    @Test
    public void daoGetIdFromData(){
        BrandPojo p = new BrandPojo();
        for(int i=0;i<4;i++) {
            daoInsertHelper();
        }

        p = daoInsertHelper();
        List<BrandPojo> pList = dao.selectAll();
        Assert.assertEquals(pList.get(4).getId(),dao.getIdFromData(p.getBrand(),p.getCategory()));
        Assert.assertEquals(-1,dao.getIdFromData(p.getBrand()+getRandomString(),p.getCategory()));
    }

    @Test
    public void daoCheckBrand(){
        BrandPojo p = daoInsertHelper();
        String brand = getRandomString();
        Assert.assertTrue(!dao.check_brand(brand));
        Assert.assertTrue(dao.check_brand(p.getBrand()));
    }
    @Test
    public void daoCheckCategory(){
        BrandPojo p = daoInsertHelper();
        String category = getRandomString();
        Assert.assertTrue(!dao.check_category(category));
        Assert.assertTrue(dao.check_brand(p.getBrand()));
    }

    @Test
    public void daoUpdate(){
        dao.update();
    }

    private BrandPojo daoInsertHelper(){
        BrandPojo p = new BrandPojo();
        String category = getRandomString();
        String brand = getRandomString();
        p.setCategory(category);
        p.setBrand(brand);
        dao.insert(p);
        return p;
    }
}
