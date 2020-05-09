package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@GetMapping
	public String getIndex() {
		return "index";
	}

	@GetMapping("/admin-line")
	public String getAdminLine() {
		return "admin-line";
	}
}
