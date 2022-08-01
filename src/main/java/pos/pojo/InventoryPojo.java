package pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"barcode"}) }, name="pos_inventory_pojo")
public class InventoryPojo extends AbstractPojo{

    @Id
    @TableGenerator(name=INVENTORY_GENERATOR, initialValue = INVENTORY_INITIAL_VALUE)
    @GeneratedValue(strategy= GenerationType.TABLE, generator = INVENTORY_GENERATOR)
    private Integer id;

    @Column(nullable = false)
    private Integer productId;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private Integer quantity;

}
