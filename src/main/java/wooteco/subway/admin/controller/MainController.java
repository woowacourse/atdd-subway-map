package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/admin-station")
    public String showAdminStation() {
        return "admin-station";
    }

    @GetMapping("/admin-line")
    public String showAdminLine() {
        return "admin-line";
    }

    @GetMapping("/admin-edge")
    public String showAdminEdge() {
        return "admin-edge";
    }

}
