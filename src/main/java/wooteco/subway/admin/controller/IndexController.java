package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/adminLine")
    public String line() {
        return "admin-line";
    }

    @GetMapping("adminStation")
    public String station() {
        return "admin-station";
    }

    @GetMapping("/adminLineStation")
    public String lineStation() {
        return "admin-edge";
    }
}
