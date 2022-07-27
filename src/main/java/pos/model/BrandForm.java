package pos.model;

import javax.validation.constraints.NotNull;

public class BrandForm {
    @NotNull(message = "brand cannot be null")
    private String brand;
    @NotNull(message = "category cannot be null")
    private String category;

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
}
