package pos.dao;

import org.springframework.stereotype.Repository;
import pos.pojo.OrderPojo;

import java.util.List;

@Repository
public class OrderDao extends AbstractDao{

    public void add(OrderPojo p){
        this.add(p);
    }

    public OrderPojo select(int id){
        return select(OrderPojo.class,id);
    }

    public List<OrderPojo> selectAll(){
        return selectAll(OrderPojo.class);
    }

}
