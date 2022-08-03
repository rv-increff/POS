package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class InventoryForm {

    @NotNull
    private String barcode;

    @NotNull
    private Integer quantity;

}
