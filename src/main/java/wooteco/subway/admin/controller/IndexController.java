package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private IndexController() {
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/adminLine")
    public String adminLine() {
        return "admin-line";
    }

    @GetMapping("/adminStation")
    public String adminStation() {
        return "admin-station";
    }

    @GetMapping("/adminEdge")
    public String adminEdge() {
        return "admin-edge";
    }
}
