package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class InventoryData {

    @NotNull(message = "id cannot be null")
    private Integer id;

    @NotNull(message = "productId cannot be null")
    private Integer productId;

    @NotBlank(message = "barcode cannot be null")
    private String barcode;

    @NotNull(message = "quantity cannot be null")
    private Integer quantity;


}
