package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class InventoryForm {

    @NotNull(message = "barcode cannot be null")
    private String barcode;

    @NotNull(message = "quantity cannot be null")
    private Integer quantity;

}
