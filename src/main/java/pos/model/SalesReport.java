package pos.model;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class SalesReport {

    private String brand;
    private String category;
    private Long quantity;
    private Double revenue;

    public SalesReport(String brand, String category, Long quantity, Double revenue){
//        super();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        this.brand = brand;
        this.category = category;
        this.quantity = quantity;
        this.revenue = Double.valueOf(decimalFormat.format(revenue));
    }
    public SalesReport(){
    }

}
