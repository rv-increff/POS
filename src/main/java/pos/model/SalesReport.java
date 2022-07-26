package pos.model;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.Date;


public class SalesReport {
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private String brand;


    private String category;


    private Long quantity;


    private Double revenue;

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

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

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
