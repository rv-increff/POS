package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class BrandForm {
    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotNull(message = "category cannot be null")
    private String category;

}
