package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.repository.LineRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;

@RestController
public class LineController {

    @Autowired
    private LineRepository lineRepository;

    @PostMapping("/lines")
    public ResponseEntity<?> create(
            @RequestBody LineRequest request
            ) throws URISyntaxException {
        String name = request.getName();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        int intervalTime = request.getIntervalTime();

        Line line = new Line(name, startTime, endTime, intervalTime);
        Line created = lineRepository.save(line);

        final URI url = new URI("/lines/" + created.getId());
        return ResponseEntity.created(url).body("{}");
    }

}
