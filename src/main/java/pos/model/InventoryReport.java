package pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReport {

    private String brand;
    private String category;
    private Integer quantity;

    public InventoryReport(String brand, String category, int quantity){
        this.brand = brand;
        this.category = category;
        this.quantity = quantity;
    }

}
