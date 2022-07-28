package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class InventoryUpdateForm {

    @NotNull(message = "id cannot be null")
    private Integer id;

    @NotNull(message = "quantity cannot be null")
    private Integer quantity;
}
