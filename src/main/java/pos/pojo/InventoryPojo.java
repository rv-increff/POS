package pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"barcode"}) }, name="pos_inventory_pojo")
public class InventoryPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private Integer quantity;

}
