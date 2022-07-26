package pos.model;

import javax.validation.constraints.NotNull;

public class InventoryInsertForm {

    @NotNull(message = "barcode cannot be null")
    private String barcode;
    private @NotNull(message = "quantity cannot be null") Integer quantity;

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
}
