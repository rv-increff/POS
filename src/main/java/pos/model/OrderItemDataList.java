package pos.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class OrderItemDataList {
    private List<OrderItemData> orderItem;
    private String time;
    private Double total;
    private int orderId;

    public OrderItemDataList(){

    }

    public OrderItemDataList(List<OrderItemData> p,Date time,Double total, int orderId){
        this.orderItem = new ArrayList<OrderItemData>();

        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(time);

        this.orderId = orderId;
        this.total = total;
        for(OrderItemData i : p){

            this.orderItem.add(i);
        }
    }

    public List<OrderItemData> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItemData> orderItem) {
        this.orderItem = orderItem;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
