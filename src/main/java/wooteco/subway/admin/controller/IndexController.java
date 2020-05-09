package wooteco.subway.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import wooteco.subway.admin.repository.StationRepository;

@Controller
public class IndexController {
    private final StationRepository stationRepository;

    public IndexController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
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
