package pos.pojo;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "brand", "category" }) },name="BrandPojo")
public class BrandPojo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @NotBlank
    private String brand;
    @NotBlank //TODO don't use validation lvl check here
    private String category;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
      this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
