package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class BrandForm {
    @NotNull
    private String brand;

    @NotNull
    private String category;

}
