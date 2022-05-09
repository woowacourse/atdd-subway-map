package wooteco.subway.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SimpleLineRequest;
import wooteco.subway.dto.SimpleLineResponse;
import wooteco.subway.service.LineService;

import javax.validation.Valid;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<SimpleLineResponse> createLine(@Valid  @RequestBody LineRequest lineRequest) {
        SimpleLineResponse lineResponse = lineService.create(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

//    @GetMapping("/lines")
//    public ResponseEntity<List<SimpleLineResponse>> findAllLine() {
//        return ResponseEntity.ok(lineService.findAll());
//    }
//
//    @GetMapping("/lines/{id}")
//    public ResponseEntity<SimpleLineResponse> findLineById(@PathVariable Long id) {
//        return ResponseEntity.ok(lineService.findById(id));
//    }
//
//    @PutMapping("/lines/{id}")
//    public void updateLine(@PathVariable Long id,@Valid @RequestBody SimpleLineRequest lineRequest) {
//        lineService.update(id, lineRequest);
//    }
//
//    @DeleteMapping("/lines/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        lineService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}
