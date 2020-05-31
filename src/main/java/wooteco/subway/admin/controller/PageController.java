package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin-line.html")
    public String adminLine() {
        return "admin-line";
    }

    @GetMapping("/admin-edge.html")
    public String adminEdge() {
        return "admin-edge";
    }

    @GetMapping("/admin-station.html")
    public String adminStation() {
        return "admin-station";
    }
}
