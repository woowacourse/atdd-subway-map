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

    @GetMapping("/adminStation")
    public String adminStation(){
        return "admin-station";
    }

    @GetMapping("/adminLine")
    public String adminLine(){
        return "admin-line";
    }

    @GetMapping("/adminEdge")
    public String adminEdge(){
        return "admin-edge";
    }
}
