package pos.model;

import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.util.Date;


public class OrderData {

    @NotNull(message = "id cannot be null")
    private int id;

    @NotNull
    private Date time;

    @NotNull
    private boolean orderPlaced=false;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isOrderPlaced() {
        return orderPlaced;
    }

    public void setOrderPlaced(boolean orderPlaced) {
        this.orderPlaced = orderPlaced;
    }
}
