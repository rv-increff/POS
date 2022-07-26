package pos.model;

public class InventoryReport {

    private String brand;


    private String category;


    private Integer quantity;

    public String getBrand() {
        return brand;
    }

    public InventoryReport(String brand, String category, int quantity){
        this.brand = brand;
        this.category = category;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
