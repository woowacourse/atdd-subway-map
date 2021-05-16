package wooteco.subway.web.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.facade.LineFacade;
import wooteco.subway.web.dto.LineRequest;
import wooteco.subway.web.dto.LineResponse;
import wooteco.subway.web.dto.LineUpdateRequest;

@RestController
@RequestMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineController {

    private final LineFacade lineFacade;

    public LineController(LineFacade lineFacade) {
        this.lineFacade = lineFacade;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody @Valid LineRequest lineRequest) {
        Long lineId = lineFacade.add(lineRequest);
        LineResponse lineResponse = lineFacade.findById(lineId);

        return ResponseEntity
                .created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> list() {
        List<LineResponse> lineResponses = lineFacade.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> find(@PathVariable Long id) {
        LineResponse lineResponse = lineFacade.findById(id);

        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
            @RequestBody @Valid LineUpdateRequest lineRequest) {
        lineFacade.update(id, lineRequest.toEntity());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineFacade.delete(id);

        return ResponseEntity.noContent().build();
    }
}
