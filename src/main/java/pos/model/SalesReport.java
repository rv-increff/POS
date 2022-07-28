package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.Date;

@Getter
@Setter
public class SalesReport {
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private String brand;
    private String category;
    private Long quantity;
    private Double revenue;

    public SalesReport(String brand, String category, Long quantity, Double revenue){
//        super();
        this.brand = brand;
        this.category = category;
        this.quantity = quantity;
        this.revenue = Double.valueOf(decimalFormat.format(revenue));
    }
    public SalesReport(){
    }

}
