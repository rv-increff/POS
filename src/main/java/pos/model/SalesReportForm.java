package pos.model;


public class SalesReportForm {
//    @NotNull(message = "brand cannot be null")
    private String brand;
//    @NotNull(message = "category cannot be null")
    private String category;

//    @NotNull(message = "from date cannot be null")
    private String from;

//    @NotNull(message = "from date cannot be null")
    private String to;

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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return this.getBrand() + ", "+  this.getCategory() +  ", "+this.getFrom() +  ", "+ this.getTo();
    }
}
