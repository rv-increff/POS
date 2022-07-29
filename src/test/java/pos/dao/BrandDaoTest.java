package pos.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dto.QaConfig;
import pos.pojo.BrandPojo;

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
        dao.add(p);
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
    public void daoUnique(){
        String brand = getRandomString();
        String category = getRandomString();

        Assert.assertNull(dao.selectByBrandCategory(brand, category));

        BrandPojo p = daoInsertHelper();

        Assert.assertNotNull(dao.selectByBrandCategory(p.getBrand(), p.getCategory()));


    }

    @Test
    public void daoGetIdFromData(){
        BrandPojo p = new BrandPojo();
        for(int i=0;i<4;i++) {
            daoInsertHelper();
        }
        p = daoInsertHelper();
        List<BrandPojo> pList = dao.selectAll();
        Assert.assertEquals(pList.get(4).getId(), dao.selectByBrandCategory(p.getBrand(),p.getCategory()).getId());
        Assert.assertNull(dao.selectByBrandCategory(p.getBrand()+getRandomString(),p.getCategory()));
    }

    @Test
    public void daoCheckBrand(){
        BrandPojo p = daoInsertHelper();
        String brand = getRandomString();
        Assert.assertNull(dao.selectByBrand(brand));
        Assert.assertNotNull(dao.selectByBrand(p.getBrand()));
    }
    @Test
    public void daoCheckCategory(){
        BrandPojo p = daoInsertHelper();
        String category = getRandomString();
        Assert.assertNull(dao.selectByCategory(category));
        Assert.assertNotNull(dao.selectByCategory(p.getCategory()));
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
        dao.add(p);
        return p;
    }
}
