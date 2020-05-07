package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/stations")
    public String stations() {
        return "admin-station";
    }

    @GetMapping("/lines")
    public String lines() {
        return "admin-line";
    }

}
