package pos.dto;

import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import pos.spring.SpringConfig;

@Configuration
@ComponentScan(//
		basePackages = { "pos" }, //
		excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = { SpringConfig.class })//
)
@PropertySources({ //
		@PropertySource(value = "classpath:./pos/test.properties") //
})
public class QaConfig {


}
