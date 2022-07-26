package pos.pojo;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="ProductPojo",uniqueConstraints = { @UniqueConstraint(columnNames = { "barcode" }) })
public class ProductPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @NotNull
    private int brandPojoId; //TODO change to brandId
    @NotBlank
    private String barcode;
    @NotNull
    private String brand;
    @NotNull
    private String category;
    @NotBlank
    private String name;
    @Min(value = 0,message = "MRP must be greater than 0")
    private @NotNull Double mrp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @NotNull Double getMrp() {
        return mrp;
    }

    public void setMrp(@NotNull Double mrp) {
        this.mrp = mrp;
    }

    public int getBrandPojoId() {
        return brandPojoId;
    }

    public void setBrandPojoId(int brandPojoId) {
        this.brandPojoId = brandPojoId;
    }
}
