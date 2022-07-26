package pos.pojo;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="InventoryPojo",uniqueConstraints = { @UniqueConstraint(columnNames = { "barcode" }) })
public class InventoryPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @NotNull
    private int productId;

    @NotBlank
    private String barcode;
//    @Min(value = 1, message = "quantity must be greater than 1")//TODO don't use min
    private Integer quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public  Integer getQuantity() {
        return quantity;
    }

    public void setQuantity( Integer quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
