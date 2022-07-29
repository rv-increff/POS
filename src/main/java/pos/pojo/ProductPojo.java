package pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name="pos_product_pojo",uniqueConstraints = { @UniqueConstraint(columnNames = { "barcode" }) })
public class ProductPojo {
    @TableGenerator(name="productGen", allocationSize=1,initialValue = 100000)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE,generator = "productGen")
    private Integer id;
    @Column(nullable = false)
    private Integer brandId;

    @Column(nullable = false)
    private String barcode;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double mrp;
}
