package pos.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Date;
@Getter
@Setter
@Entity
@Table(name = "pos_order_pojo")
public class OrderPojo {
    @TableGenerator(name="orderGen", allocationSize=1,initialValue = 100000)
    @Id
    @GeneratedValue(strategy= GenerationType.TABLE,generator = "orderGen")
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime time;

    @Column(nullable = false)
    private Boolean orderPlaced=false;
}
