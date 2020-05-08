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
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

@RestController
public class LineStationController {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

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

        final Line line = lineRepository.findByName(lineName)
                .orElseThrow(NoSuchElementException::new);
        final Station preStation = stationRepository.findByName(preStationName)
                .orElseThrow(NoSuchElementException::new);
        final Station station = stationRepository.findByName(stationName)
                .orElseThrow(NoSuchElementException::new);

        LineStation lineStation = new LineStation(
                line.getId(),
                preStation.getId(),
                station.getId(),
                distance,
                duration
        );
        line.addLineStation(lineStation);
        lineRepository.save(line);

        final URI url = new URI("/lineStation/" + line.getId() + preStation.getId() + station.getId());
        return ResponseEntity.created(url)
                .body(lineStation);
    }
}
