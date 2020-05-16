package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String station() {
        return "index";
    }

    @GetMapping("/admin-station")
    public String index() {
        return "admin-station";
    }

    @GetMapping("/admin-edge")
    public String edge() {
        return "admin-edge";
    }

    @GetMapping("/admin-line")
    public String line() {
        return "admin-line";
    }

}
