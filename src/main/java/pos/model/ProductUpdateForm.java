package pos.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProductUpdateForm {
    @NotNull(message = "id cannot be null")
    private int id;

    @NotBlank(message = "barcode cannot be blank")
    private String barcode;

    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotBlank(message = "category cannot be null")
    private String category;

    @NotBlank(message = "name cannot be null")
    private String name;

    private @NotNull(message = "mrp cannot be null") Double mrp;

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

    public @NotNull(message = "mrp cannot be null") Double getMrp() {
        return mrp;
    }

    public void setMrp(@NotNull(message = "mrp cannot be null") Double mrp) {
        this.mrp = mrp;
    }
}
