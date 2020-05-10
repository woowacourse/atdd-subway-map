package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin-station")
    public String adminStation(){
        return "admin-station";
    }

    @GetMapping("/admin-line")
    public String adminLine(){
        return "admin-line";
    }

    @GetMapping("/admin-edge")
    public String adminEdge(){
        return "admin-edge";
    }
}
