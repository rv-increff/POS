package pos.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.BrandDao;
import pos.model.BrandData;
import pos.model.BrandInsertForm;
import pos.pojo.BrandPojo;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static pos.util.RandomUtil.getRandomString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class BrandServicesTest{

    @Resource
    private BrandServices services;
    @Resource
    private BrandDao dao;


    @Test
    public void brandGetAll() throws ApiException {
        for(int i=0;i<5;i++)daoInsertHelper();

        List<BrandData> plist = services.getAll();
        Assert.assertEquals(5,plist.size());
    }

    @Test
    public void brandGet() throws ApiException {
        for(int i=0;i<5;i++)daoInsertHelper();

        List<BrandPojo> brandPojoList = dao.selectAll();
        int index  = brandPojoList.get(4).getId();
        BrandData p = services.get(index);
        Assert.assertEquals(index,p.getId());

        try {
            services.get(index+1);
        }catch (Exception e){
            Assert.assertEquals("Brand with given id does not exist ,id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void brandAdd() throws ApiException {
        BrandInsertForm p = new BrandInsertForm();

        int prevSize = dao.selectAll().size();

        String brand = getRandomString();
        String category = getRandomString();
        p.setBrand(brand);
        p.setCategory(category);
        services.add(p);

        int curSize = dao.selectAll().size();
        Assert.assertEquals(prevSize+1,curSize);

    }
    @Test
    public void brandAddEmptyObject() throws ApiException {
        BrandInsertForm p = new BrandInsertForm();
        try {
            services.add(p);
        }catch (Exception e){
            Assert.assertEquals("brand or category cannot be null",e.getMessage());
        }

    }

    @Test
    public void brandAddErrorUnique() throws ApiException {
        BrandInsertForm p = new BrandInsertForm();

        int prevSize = dao.selectAll().size();

        String brand = getRandomString();
        String category = getRandomString();
        p.setBrand(brand);
        p.setCategory(category);
        services.add(p);

        try{
            services.add(p);
        }catch (Exception e){
            Assert.assertEquals("Brand and category pair should be unique",e.getMessage());
        }

    }

    @Test
    public void brandAddErrorUniqueNormalize() throws ApiException {
        BrandInsertForm p = new BrandInsertForm();

        int prevSize = dao.selectAll().size();

        String brand = getRandomString();
        String category = getRandomString();
        p.setBrand(brand);
        p.setCategory(category);
        services.add(p);

        try{
            p.setBrand(p.getBrand().toLowerCase(Locale.ROOT));
            p.setCategory(p.getCategory().toLowerCase(Locale.ROOT));
            services.add(p);
        }catch (Exception e){
            Assert.assertEquals("Brand and category pair should be unique",e.getMessage());
        }

    }

    @Test
    public void bulkAdd() throws ApiException {
        List<BrandInsertForm> pList = new ArrayList<>();
        int n = 5;
        for(int i=0;i<n;i++){
            BrandInsertForm p = new BrandInsertForm();
            p.setBrand(getRandomString());
            p.setCategory(getRandomString());
            pList.add(p);
        }

        services.bulkAdd(pList);
        int i = dao.selectAll().size();
        Assert.assertEquals(n,i);
    }
    @Test
    public void bulkAddErrorUnique() throws ApiException {
        List<BrandInsertForm> pList = new ArrayList<>();

        String brand = getRandomString().toLowerCase();
        String category = getRandomString().toLowerCase();
        BrandPojo p1 = new BrandPojo();
        p1.setBrand(brand);
        p1.setCategory(category);
        dao.insert(p1);

        BrandInsertForm p = new BrandInsertForm();
        p.setBrand(brand);
        p.setCategory(category);
        pList.add(p);
        int n = 5;
        for(int i=0;i<n;i++){
            p = new BrandInsertForm();
            p.setBrand(getRandomString());
            p.setCategory(getRandomString());
            pList.add(p);
        }
        try{
            services.bulkAdd(pList);
        }catch (Exception e){
            String expErr = "Error : row -> " + (1) + " "  + brand + " - " +  category + " pair should be unique<br>";
            Assert.assertEquals(expErr,e.getMessage());
        }
        Assert.assertEquals(1,dao.selectAll().size());

    }

    @Test
    public void bulkAddErrorEmpty() throws ApiException {
        List<BrandInsertForm> pList = new ArrayList<>();

        BrandInsertForm p = new BrandInsertForm();
        pList.add(p);
        int n = 5;
        for(int i=0;i<n;i++){
            p = new BrandInsertForm();
            p.setBrand(getRandomString());
            p.setCategory(getRandomString());
            pList.add(p);
        }
        try{
            services.bulkAdd(pList);
        }catch (Exception e){
            String expErr = "Error : row -> " + (1) + " brand or category cannot be empty<br>";
            Assert.assertEquals(expErr,e.getMessage());
        }
        Assert.assertEquals(0,dao.selectAll().size());

    }

    @Test
    public void brandUpdate() throws ApiException {
        BrandPojo p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();

        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(brand);
        brandData.setCategory(p.getCategory());
        brandData.setId(id);

        services.update(brandData);
    }

    @Test
    public void brandUpdateErrorUnique(){
        BrandPojo p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();

        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(p.getBrand());
        brandData.setCategory(p.getCategory());
        brandData.setId(id);

        try{
            services.update(brandData);
        } catch (Exception e) {
            Assert.assertEquals(p.getBrand() + " -- " +  p.getCategory() + " pair should be unique",e.getMessage());
        }
    }

    @Test
    public void brandUpdateErrorEmptyObject(){
        BrandPojo p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();

        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(null);
        brandData.setCategory(null);
        brandData.setId(id);

        try{
            services.update(brandData);
        } catch (Exception e) {
            Assert.assertEquals("brand or category cannot be null",e.getMessage());
        }
    }

    @Test
    public void brandUpdateErrorWrongId(){
        int id = 1;
        String brand = getRandomString();
        BrandData brandData = new BrandData();
        brandData.setBrand(brand);
        brandData.setCategory(brand);
        brandData.setId(id);

        try{
            services.update(brandData);
        } catch (Exception e) {
            Assert.assertEquals("Brand with given id does not exist ,id : " + id,e.getMessage());
        }
    }

    @Test
    public void brandDelete() throws ApiException {
        BrandPojo  p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();
        services.delete(id);
    }
    @Test
    public void brandDeleteErrorId() throws ApiException {
        BrandPojo  p = daoInsertHelper();
        int id = dao.selectAll().get(0).getId();
        try {
            services.delete(id+1);
        }catch (Exception e){
            String err = "Brand with given id does not exist ,id : " + (id+1);
            Assert.assertEquals(err,e.getMessage());

        }
    }

//    @Test
//    public void brandDeleteErrorProduct() throws ApiException {
//        BrandPojo  p = daoInsertHelper();
//        int id = dao.selectAll().get(0).getId();
//        try {
//            services.delete(id+1);
//        }catch (Exception e){
//            String err = "Brand with given id does not exist ,id : " + (id+1);
//            Assert.assertEquals(err,e.getMessage());
//
//        }
//    }



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
