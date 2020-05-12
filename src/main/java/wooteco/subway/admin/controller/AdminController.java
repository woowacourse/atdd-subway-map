package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
public class AdminController {
	@GetMapping("line")
	public String showAdminLinePage() {
		return "/admin-line";
	}

	@GetMapping("edge")
	public String showAdminEdge() {
		return "/admin-edge";
	}

	@GetMapping("station")
	public String showAdminStation() {
		return "/admin-station";
	}
}
