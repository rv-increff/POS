package pos.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class BrandData {

    @NotNull(message = "id cannot be null")
    private Integer id;

    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotNull(message = "brand cannot be null")
    private String category;

}
