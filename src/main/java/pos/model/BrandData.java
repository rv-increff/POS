package pos.model;


import javax.validation.constraints.NotNull;

public class BrandData {

    @NotNull(message = "id cannot be null")
    private int id;
    @NotNull(message = "brand cannot be null")
    private String brand;
    @NotNull(message = "brand cannot be null")
    private String category;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
