package pos.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProductForm {

    @NotBlank(message = "barcode cannot be empty")
    private String barcode;

    @NotBlank(message = "brand cannot be empty")
    private String brand;

    @NotBlank(message = "category cannot be empty")
    private String category;

    @NotBlank(message = "name cannot be empty")
    private String name;

    private @NotNull Double mrp;


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
}
