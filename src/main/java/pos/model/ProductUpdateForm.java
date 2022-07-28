package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class ProductUpdateForm {
    @NotNull(message = "id cannot be null")
    private Integer id;

    @NotBlank(message = "barcode cannot be blank")
    private String barcode;

    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotBlank(message = "category cannot be null")
    private String category;

    @NotBlank(message = "name cannot be null")
    private String name;

    @NotNull(message = "mrp cannot be null")
    private Double mrp;


}
