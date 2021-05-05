package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.assembler.Assembler;
import wooteco.subway.exception.DuplicatedLineNameException;
import wooteco.subway.exception.VoidLineException;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController() {
        Assembler assembler = new Assembler();
        this.lineService = assembler.getLineService();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            LineDto lineDto = new LineDto(lineRequest.getName(), lineRequest.getColor());
            LineDto createdLineDto = lineService.createLine(lineDto);
            LineResponse lineResponse = new LineResponse(createdLineDto.getId(),
                createdLineDto.getName(), createdLineDto.getColor());
            return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
        } catch (DuplicatedLineNameException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineDto> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        try {
            LineDto lineDto = lineService.findOne(id);
            LineResponse lineResponse = new LineResponse(lineDto.getId(), lineDto.getName(),
                lineDto.getColor());
            return ResponseEntity.ok().body(lineResponse);
        } catch (VoidLineException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest,
        @PathVariable Long id) {
        try {
            LineDto lineDto = new LineDto(id, lineRequest.getName(), lineRequest.getColor());
            lineService.update(id.intValue(), lineDto);
            return ResponseEntity.ok().build();
        } catch (VoidLineException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        try {
            lineService.delete(id);
            return ResponseEntity.ok().build();
        } catch (VoidLineException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
