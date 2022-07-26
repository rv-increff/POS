package pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@Getter
@Setter
@MappedSuperclass
public class AbstractPojo {

    @Version
    private Integer version;

}
