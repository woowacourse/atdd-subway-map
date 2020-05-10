package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RenderController {

    @GetMapping
    public String index() {
        return "index";
    }

}
