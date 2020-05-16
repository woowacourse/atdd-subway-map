package wooteco.subway.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PageController {

    private final LineService lineService;
    private final StationService stationService;

    public PageController(final LineService lineService, final StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin-line")
    public ModelAndView adminLine() {
        ModelAndView mv = new ModelAndView("admin-line");
        mv.addObject("lines", lineService.findAllLines());
        return mv;
    }

    @GetMapping("/admin-station")
    public ModelAndView adminStation() {
        ModelAndView mv = new ModelAndView("admin-station");
        mv.addObject("stations", stationService.findAllStations());
        return mv;
    }

    @GetMapping("/admin-edge")
    public ModelAndView adminEdge() {
        ModelAndView mv = new ModelAndView("admin-edge");

        List<LineResponse> lines = lineService.findAllLines().stream()
                .map(line -> lineService.getLineWithStationsById(line.getId()))
                .collect(Collectors.toList());

        List<StationResponse> stations = stationService.findAllStations();

        mv.addObject("lines", lines);
        mv.addObject("stations", stations);
        return mv;
    }

}
