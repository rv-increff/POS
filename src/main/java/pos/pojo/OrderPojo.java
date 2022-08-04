package pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static pos.pojo.TableConstants.ORDER_GENERATOR;
import static pos.pojo.TableConstants.ORDER_INITIAL_VALUE;

@Getter
@Setter
@Entity
@Table(name = "pos_order_pojo")
public class OrderPojo extends AbstractPojo{

    @TableGenerator(name=ORDER_GENERATOR, initialValue = ORDER_INITIAL_VALUE)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator = ORDER_GENERATOR)
    private Integer id; //TODO make a common file table consts and all names and table data as final static

    @Column(nullable = false)
    private ZonedDateTime time;

    @Column(nullable = false)
    private Boolean orderPlaced=false;
}
