package pos.model;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class OrderItemData {

    @NotNull(message = "Id cannot be empty")
    private Integer id;

    @NotNull(message = "OrderId cannot be empty")
    private Integer orderId;

    @NotNull(message = "productId cannot be empty")
    private Integer productId;

    @Min(value = 1, message = "quantity must be greater than 1") @NotNull(message = "quantity cannot be empty")
    private Integer quantity;

    @Min(value = 0,message = "sellingPrice must be greater than 0")
    @NotNull(message = "sellingPrice cannot be empty")
    private Double sellingPrice;

}
