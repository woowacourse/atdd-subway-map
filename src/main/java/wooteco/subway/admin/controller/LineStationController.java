package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.LineStationService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineStationController {

    @Autowired
    private LineStationService lineStationService;

    @Autowired
    private LineService lineService;

    @GetMapping("/admin-edge")
    public ModelAndView adminEdge() {
        ModelAndView mv = new ModelAndView("admin-edge");
        List<Line> lines = lineService.showLines();

        List<LineResponse> lineResponses = lines.stream()
                .map(line -> lineService.findLineWithStationsById(line.getId()))
                .collect(Collectors.toList());

        mv.addObject("lines", lineResponses);
        return mv;
    }

    @PostMapping("/lineStation")
    public ResponseEntity<?> create(
            @RequestBody LineStationCreateRequest request
    ) throws URISyntaxException {

        final Long lineName = request.getLine();
        final Long preStationName = request.getPreStationId();
        final Long stationName = request.getStationId();
        final int distance = request.getDistance();
        final int duration = request.getDuration();

        LineStation lineStation = lineStationService.createLineStation(
                lineName, preStationName, stationName, distance, duration);

        final URI url = new URI("/lineStation/" + lineStation.getCustomId());
        return ResponseEntity.created(url)
                .body(lineStation);
    }
}
