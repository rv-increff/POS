package pos.dto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import pos.dao.OrderDao;
import pos.model.OrderData;
import pos.pojo.OrderPojo;
import pos.spring.ApiException;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static pos.util.RandomUtil.getRandomNumber;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration("src/test/webapp")
@Transactional
public class OrderDtoTest {

    @Resource
    private OrderDto dto;
    @Resource
    private OrderDao dao;


    @Test
    public void getAll() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();

        List<OrderData> plist = dto.getAll();
        Assert.assertEquals(n,plist.size());
    }
    @Test
    public void get() throws ApiException {
        int n =5;
        for(int i=0;i<n;i++)daoInsertHelper();
        List<OrderPojo> orderPojoList = dao.selectAll();

        int index  = orderPojoList.get(n-1).getId();
        OrderData p = dto.get(index);
        Assert.assertEquals(Optional.of(index),Optional.of(p.getId()));
        try {
            dto.get(index+1);
        }catch (ApiException e){
            Assert.assertEquals("Order with given id does not exist, id : " + (index+1),e.getMessage());
        }
    }

    @Test
    public void add() throws ApiException {
        dto.add();
    }

    @Test
    public void updateOrderStatus() throws ApiException {
        Integer id = getRandomNumber();
        try{
            dto.updateOrderStatusPlaced(id);
        }catch (ApiException e){
            Assert.assertEquals("Order with given id does not exist, id : " + id,e.getMessage());
        }
        daoInsertHelper();
        id = dao.selectAll().get(0).getId();
        dto.updateOrderStatusPlaced(id);
    }
    private OrderPojo daoInsertHelper(){
        OrderPojo p = new OrderPojo();
        Date now = new Date();
        p.setTime(now);
        dao.add(p);
        return p;
    }
}

