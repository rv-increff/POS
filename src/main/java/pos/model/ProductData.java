package pos.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;
import pos.pojo.BrandPojo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class ProductData {
    @NotNull(message = "id cannot be null")
    private Integer id;

    @NotNull(message = "brandPojo id cannot be null")
    private Integer BrandPojoId;

    @NotNull(message = "barcode cannot be null")
    private String barcode;

    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotNull(message = "category cannot be null")
    private String category;

    @NotNull(message = "name cannot be null")
    private String name;

    @NotNull(message = "mrp cannot be null")
    private Double mrp;
}
