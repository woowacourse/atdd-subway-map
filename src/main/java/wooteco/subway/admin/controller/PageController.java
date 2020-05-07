package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/admin-station.html")
    public String index() {
        return "admin-station";
    }
    @GetMapping("/admin-edge.html")
    public String edge() {
        return "admin-edge";
    }
    @GetMapping("/admin-line.html")
    public String line() {
        return "admin-line";
    }
    @GetMapping("/")
    public String station() {
        return "index";
    }

}
