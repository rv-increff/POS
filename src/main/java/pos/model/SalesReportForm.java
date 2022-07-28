package pos.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class SalesReportForm {
    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotNull(message = "category cannot be null")
    private String category;

    @NotNull(message = "from date cannot be null")
    private String from;

    @NotNull(message = "from date cannot be null")
    private String to;

    @Override
    public String toString() {
        return this.getBrand() + ", "+  this.getCategory() +  ", "+this.getFrom() +  ", "+ this.getTo();
    }
}
