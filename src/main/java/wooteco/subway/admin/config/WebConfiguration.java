package wooteco.subway.admin.config;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfiguration implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/admin-station").setViewName("admin-station");
		registry.addViewController("/admin-line").setViewName("admin-line");
		registry.addViewController("/admin-edge").setViewName("admin-edge");
	}
}
