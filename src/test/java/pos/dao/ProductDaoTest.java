package pos.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.pojo.ProductPojo;
import pos.services.QaConfig;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.util.List;

import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class ProductDaoTest {

    @Resource
    ProductDao dao;

    @Test
    public void daoInsert(){
        ProductPojo p = new ProductPojo();
        p.setCategory(getRandomString());
        p.setBrand(getRandomString());
        p.setName(getRandomString());
        p.setBarcode(getRandomString());
        p.setBrandId(1);
        dao.insert(p);
    }

    @Test
    public void daoSelectAll(){
        int n = 5;
        for(int i=0;i<n;i++) {
            daoInsertHelper();
        }
        List<ProductPojo> plist = dao.selectAll();
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
        ProductPojo p = daoInsertHelper();
        Assert.assertNull(dao.selectFromBarcode(getRandomString()));
        Assert.assertNotNull(dao.selectFromBarcode(p.getBarcode()));
    }
    @Test
    public void daoUniqueUpdate(){
        ProductPojo p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();

        Assert.assertNull(dao.selectFromBarcodeNotEqualId(getRandomString(),0));
        Assert.assertNotNull(dao.selectFromBarcodeNotEqualId(p.getBarcode(),id+1)); //barcode same but id different
    }
    @Test
    public void daoGetIdFromBarcode(){
        ProductPojo p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();
        Assert.assertEquals(id,dao.selectFromBarcode(p.getBarcode()).getId());
        Assert.assertNull(dao.selectFromBarcode(getRandomString()));
    }

    @Test
    public void daoCheckProductId(){
        ProductPojo p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();
        Assert.assertEquals(id,dao.select(id).getId());
        Assert.assertNull(dao.select(id+1));
    }

    @Test
    public void daoUpdate(){
        dao.update();
    }

    @Test
    public void daoCheckIfBrandExist(){
        daoInsertHelper();
        int brandId = dao.selectAll().get(0).getBrandId();
        Assert.assertNotNull(dao.selectFromBrand(brandId));
        Assert.assertEquals(0,dao.selectFromBrand(brandId+1).size());
    }

    private ProductPojo daoInsertHelper(){
        ProductPojo p = new ProductPojo();
        String category = getRandomString();
        String brand = getRandomString();
        String name = getRandomString();
        String barcode = getRandomString();
        int brandId = (int) (Math.random()*100%(20));
        double mrp = Math.random()*100%(20);
        p.setCategory(category);
        p.setBrand(brand);
        p.setBarcode(barcode);
        p.setName(name);
        p.setBrandId(brandId);
        p.setMrp(mrp);
        dao.insert(p);
        return p;
    }

}
