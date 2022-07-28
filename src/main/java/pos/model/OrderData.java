package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.util.Date;

@Getter
@Setter
public class OrderData {

    @NotNull(message = "id cannot be null")
    private Integer id;

    @NotNull
    private Date time;

    @NotNull
    private boolean orderPlaced=false;

}
