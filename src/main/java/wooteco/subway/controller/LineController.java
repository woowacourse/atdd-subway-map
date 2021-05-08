package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.exception.line.LineNotExistException;
import wooteco.subway.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        LineValidator lineValidator = new LineValidator();
        webDataBinder.addValidators(lineValidator);
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody @Valid LineRequest lineRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        LineResponse lineResponse = lineService.create(lineRequest.getColor(), lineRequest.getName());
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAllById();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> showLines(@PathVariable Long id) throws LineNotExistException {
        LineResponse lineResponse = lineService.findById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateById(id, lineRequest.getColor(), lineRequest.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
