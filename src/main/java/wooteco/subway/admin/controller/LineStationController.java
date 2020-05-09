package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.service.LineStationService;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class LineStationController {

    @Autowired
    private LineStationService lineStationService;

    @GetMapping("/admin-edge")
    public ModelAndView adminEdge() {
        ModelAndView mv = new ModelAndView("admin-edge");
        return mv;
    }

    @PostMapping("/lineStation")
    public ResponseEntity<?> create(
            @RequestBody LineStationRequest request
    ) throws URISyntaxException {

        final String lineName = request.getLineName();
        final String preStationName = request.getPreStationName();
        final String stationName = request.getStationName();
        final int distance = request.getDistance();
        final int duration = request.getDuration();

        LineStation lineStation = lineStationService.createLineStation(
                lineName, preStationName, stationName, distance, duration);

        final URI url = new URI("/lineStation/" + lineStation.getCustomId());
        return ResponseEntity.created(url)
                .body(lineStation);
    }
}
