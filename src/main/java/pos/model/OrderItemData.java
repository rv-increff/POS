package pos.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderItemData {

    @NotNull(message = "Id cannot be empty")
    private int id;
    @NotNull(message = "OrderId cannot be empty")
    private int orderId;

    @NotNull(message = "productId cannot be empty")
    private int productId;


    private @Min(value = 1, message = "quantity must be greater than 1") @NotNull(message = "quantity cannot be empty") Integer quantity;
    @Min(value = 0,message = "sellingPrice must be greater than 0")
    @NotNull(message = "sellingPrice cannot be empty")
    private double sellingPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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



    public @Min(value = 1, message = "quantity must be greater than 1") @NotNull(message = "quantity cannot be empty") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@Min(value = 1, message = "quantity must be greater than 1") @NotNull(message = "quantity cannot be empty") Integer quantity) {
        this.quantity = quantity;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
