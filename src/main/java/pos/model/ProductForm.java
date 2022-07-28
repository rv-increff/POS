package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class ProductForm {

    @NotBlank(message = "barcode cannot be empty")
    private String barcode;

    @NotBlank(message = "brand cannot be empty")
    private String brand;

    @NotBlank(message = "category cannot be empty")
    private String category;

    @NotBlank(message = "name cannot be empty")
    private String name;

    @NotNull
    Double mrp;
}
