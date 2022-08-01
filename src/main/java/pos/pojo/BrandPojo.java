package pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"brand", "category"})}, name="pos_brand_pojo")
public class BrandPojo extends AbstractPojo {

    @Id
    @TableGenerator(name=BRAND_GENERATOR, initialValue = BRAND_INITIAL_VALUE)
    @GeneratedValue(strategy= GenerationType.TABLE, generator = BRAND_GENERATOR) //TODO table
    private Integer id;  //TODO add locking

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;

}
