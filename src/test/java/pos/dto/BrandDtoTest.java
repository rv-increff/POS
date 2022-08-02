package pos.dto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.BrandDao;
import pos.model.BrandData;
import pos.model.BrandForm;
import pos.pojo.BrandPojo;
import pos.spring.ApiException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class BrandDtoTest {

    @Autowired
    private BrandDto dto;
    @Autowired
    private BrandDao dao;

    @Test
    public void brandGetAll() throws ApiException {
        for(Integer i=0;i<5;i++)daoInsertHelper();

        List<BrandData> plist = dto.getAll();
        Assert.assertEquals(5,plist.size());
    }

    @Test
    public void brandGet() throws ApiException {
        for(Integer i=0;i<5;i++)daoInsertHelper();

        List<BrandPojo> brandPojoList = dao.selectAll();
        Integer index  = brandPojoList.get(4).getId();
        BrandData p = dto.get(index);
        Assert.assertEquals(index,p.getId());

        try {
            dto.get(index+1);
        }catch (ApiException e){
            Assert.assertEquals("Brand with given id does not exist ,id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void brandAdd() throws ApiException, IllegalAccessException {
        BrandForm p = new BrandForm();

        Integer prevSize = dao.selectAll().size();

        String brand = getRandomString();
        String category = getRandomString();
        p.setBrand(brand);
        p.setCategory(category);
        dto.add(p);

        Integer curSize =  dao.selectAll().size();
        Assert.assertEquals(Optional.of(prevSize + 1),Optional.of(curSize));

    }
    @Test
    public void brandAddEmptyObject() {
        BrandForm p = new BrandForm();
        try {
            dto.add(p);
        }catch (ApiException e){
            Assert.assertEquals("brand or category cannot be null",e.getMessage());
        }

    }

    @Test
    public void brandAddErrorUnique() throws ApiException, IllegalAccessException {
        BrandForm p = new BrandForm();

        Integer prevSize = dao.selectAll().size();

        String brand = getRandomString();
        String category = getRandomString();
        p.setBrand(brand);
        p.setCategory(category);
        dto.add(p);

        try{
            dto.add(p);
        }catch (ApiException e){
            Assert.assertEquals(brand.toLowerCase() + " - " + category.toLowerCase() +  " pair already exist",e.getMessage());
        }

    }

    @Test
    public void brandAddErrorUniqueNormalize() throws ApiException {
        BrandForm p = new BrandForm();

        Integer prevSize = dao.selectAll().size();

        String brand = getRandomString();
        String category = getRandomString();
        p.setBrand(brand);
        p.setCategory(category);
        dto.add(p);

        try{
            p.setBrand(p.getBrand().toLowerCase(Locale.ROOT));
            p.setCategory(p.getCategory().toLowerCase(Locale.ROOT));
            dto.add(p);
        }catch (ApiException e){
            Assert.assertEquals(p.getBrand() + " - " + p.getCategory() + " pair already exist",e.getMessage());
        }

    }

    @Test
    public void bulkAdd() throws ApiException {
        List<BrandForm> pList = new ArrayList<>();
        Integer n = 5;
        for(Integer i=0;i<n;i++){
            BrandForm p = new BrandForm();
            p.setBrand(getRandomString());
            p.setCategory(getRandomString());
            pList.add(p);
        }

        dto.bulkAdd(pList);
        Integer i = dao.selectAll().size();
        Assert.assertEquals(n,i);
    }
    @Test
    public void bulkAddErrorUnique() throws ApiException {
        List<BrandForm> pList = new ArrayList<>();

        String brand = getRandomString().toLowerCase();
        String category = getRandomString().toLowerCase();
        BrandPojo p1 = new BrandPojo();
        p1.setBrand(brand);
        p1.setCategory(category);
        dao.add(p1);

        BrandForm p = new BrandForm();
        p.setBrand(brand);
        p.setCategory(category);
        pList.add(p);
        Integer n = 5;
        for(Integer i=0;i<n;i++){
            p = new BrandForm();
            p.setBrand(getRandomString());
            p.setCategory(getRandomString());
            pList.add(p);
        }
        try{
            dto.bulkAdd(pList);
        }catch (ApiException e){
            String expErr = "Error : row -> " + (1) + " "  + brand + " - " +  category + " pair already exist\n";
            Assert.assertEquals(expErr,e.getMessage());
        }
        Assert.assertEquals(1,dao.selectAll().size());

    }

    @Test
    public void bulkAddErrorEmpty() throws ApiException {
        List<BrandForm> pList = new ArrayList<>();

        BrandForm p = new BrandForm();
        pList.add(p);
        Integer n = 5;
        for(Integer i=0;i<n;i++){
            p = new BrandForm();
            p.setBrand(getRandomString());
            p.setCategory(getRandomString());
            pList.add(p);
        }
        try{
            dto.bulkAdd(pList);
        }catch (ApiException e){
            String expErr = "Error : row -> " + (1) + " brand or category cannot be empty\n";
            Assert.assertEquals(expErr,e.getMessage());
        }
        Assert.assertEquals(0,dao.selectAll().size());

    }

    @Test
    public void brandUpdate() throws ApiException {
        BrandPojo p = daoInsertHelper();
        Integer id = dao.selectAll().get(0).getId();

        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(brand);
        brandData.setCategory(p.getCategory());
        brandData.setId(id);
        dto.update(brandData);
    }

    @Test
    public void brandUpdateErrorUnique(){
        BrandPojo p = daoInsertHelper();
        Integer id = dao.selectAll().get(0).getId();

        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(p.getBrand());
        brandData.setCategory(p.getCategory());
        brandData.setId(id);

        try{
            dto.update(brandData);
        } catch (ApiException e) {
            Assert.assertEquals(p.getBrand() + " - " +  p.getCategory() + " pair should be unique",e.getMessage());
        }
    }

    @Test
    public void brandUpdateErrorEmptyObject(){
        BrandPojo p = daoInsertHelper();
        Integer id = dao.selectAll().get(0).getId();

        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(null);
        brandData.setCategory(null);
        brandData.setId(id);

        try{
            dto.update(brandData);
        } catch (ApiException e) {
            Assert.assertEquals("brand or category cannot be null",e.getMessage());
        }
    }

    @Test
    public void brandUpdateErrorWrongId(){
        Integer id = 1;
        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(brand);
        brandData.setCategory(brand);
        brandData.setId(id);

        try{
            dto.update(brandData);
        } catch (ApiException e) {
            Assert.assertEquals("Brand with given id does not exist ,id : " + id,e.getMessage());
        }
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
