package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin-station")
    public String admin_station() {
        return "admin-station";
    }

    @GetMapping("/admin-line")
    public String stations() {
        return "admin-line";
    }

    @GetMapping("/admin-edge")
    public String lines() {
        return "admin-edge";
    }

}
