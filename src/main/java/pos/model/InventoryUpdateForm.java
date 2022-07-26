package pos.model;

import javax.validation.constraints.NotNull;

public class InventoryUpdateForm {

    @NotNull(message = "id cannot be null")
    private int id;

    private @NotNull(message = "quantity cannot be null") Integer quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotNull(message = "quantity cannot be null") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "quantity cannot be null") Integer quantity) {
        this.quantity = quantity;
    }
}
