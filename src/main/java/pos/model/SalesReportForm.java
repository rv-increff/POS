package pos.model;

import lombok.Getter;
import lombok.Setter;
import pos.spring.ApiException;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SalesReportForm {
    @NotNull(message = "brand cannot be null")
    private String brand;

    @NotNull(message = "category cannot be null")
    private String category;

    @NotNull
    private ZonedDateTime from;

    @NotNull
    private ZonedDateTime to;

    public void setFrom(String from) throws ApiException {

        try{
            this.from = ZonedDateTime.parse(from);
        }catch (Throwable e){
            throw new ApiException("Invalid date time format must be of ZonedDateTime");
        }
    }
    public void setTo(String to) throws ApiException {

        try{
            this.to = ZonedDateTime.parse(to);
        }catch (Throwable e){
            throw new ApiException("Invalid date time format must be of ZonedDateTime");
        }
    }

    @Override
    public String toString() {
        return this.getBrand() + ", "+  this.getCategory() +  ", "+this.getFrom() +  ", "+ this.getTo();
    }
}
