package pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderData {

    @NotNull(message = "id cannot be null")
    private Integer id;

    @NotNull
    private String time;

    @NotNull
    private boolean orderPlaced=false;

}
