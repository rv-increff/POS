package pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
@Getter
@Setter
@Entity
@Table(name = "pos_order_pojo")
public class OrderPojo {
    @TableGenerator(name="orderGen", allocationSize=1,initialValue = 100000)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE,generator = "orderGen")
    private Integer id; //TODO make a common file table consts and all names and table data as final static

    @Column(nullable = false)
    private ZonedDateTime time;

    @Column(nullable = false)
    private Boolean orderPlaced=false;
}
