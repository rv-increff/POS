package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@XmlRootElement
public class OrderItemDataList {
    private List<OrderItemData> orderItem;
    private String time;
    private Double total;
    private Integer orderId;

    public OrderItemDataList(){

    }

    public OrderItemDataList(List<OrderItemData> p, ZonedDateTime time, Double total, int orderId){
        this.orderItem = new ArrayList<OrderItemData>();

//        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(time);
        this.time = time.toLocalDate().toString() + " " + time.toLocalTime().toString();

        this.orderId = orderId;
        this.total = total;
        for(OrderItemData i : p){

            this.orderItem.add(i);
        }
    }

    public List<OrderItemData> getOrderItem() {
        return orderItem;
    }

}
