package pos.model;

import org.hibernate.validator.constraints.UniqueElements;
import pos.pojo.BrandPojo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

public class ProductData {
    @NotNull(message = "id cannot be null")
    private int id;

    @NotNull(message = "brandPojo id cannot be null")
    private int BrandPojoId;

    @NotNull(message = "barcode cannot be null")
    private String barcode;

    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotNull(message = "category cannot be null")
    private String category;

    @NotNull(message = "name cannot be null")
    private String name;

    @NotNull(message = "mrp cannot be null")
    private double mrp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBrandPojoId() {
        return BrandPojoId;
    }

    public void setBrandPojoId(int BrandPojoId) {
        this.BrandPojoId = BrandPojoId;
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

    public double getMrp() {
        return mrp;
    }

    public void setMrp(double mrp) {
        this.mrp = mrp;
    }
}
