package pos.pojo;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "orderId", "productId" }) },name="OrderItemPojo")
public class OrderItemPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @NotNull
    private int orderId;

    @NotNull
    private int productId;

    private @Min(value = 1, message = "quantity must be greater than 1")
    @NotNull(message = "quantity cannot be empty")
    Integer quantity;
    @Min(value = 0,message = "sellingPrice must be greater than 0")
    @NotNull(message = "sellingPrice cannot be empty")
    private double sellingPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @Min(value = 1, message = "quantity must be greater than 1") @NotNull(message = "quantity cannot be empty") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@Min(value = 1, message = "quantity must be greater than 1") @NotNull(message = "quantity cannot be empty") Integer quantity) {
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
