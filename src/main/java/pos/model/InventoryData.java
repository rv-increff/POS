package pos.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class InventoryData {

    @NotNull(message = "id cannot be null")
    private int id;

    @NotNull(message = "productId cannot be null")
    private int productId;
    @NotBlank(message = "barcode cannot be null")
    private String barcode;

    private @NotNull(message = "quantity cannot be null") Integer quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public @NotNull(message = "quantity cannot be null") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "quantity cannot be null") Integer quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
