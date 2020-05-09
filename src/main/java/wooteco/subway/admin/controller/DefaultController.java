package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin-station")
    public String station() {
        return "admin-station";
    }

    @GetMapping("/admin-line")
    public String line() {
        return "admin-line";
    }

    @GetMapping("/admin-edge")
    public String edge() {
        return "admin-edge";
    }
}
