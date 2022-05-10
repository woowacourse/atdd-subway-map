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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> saveLine(@RequestBody LineRequest lineRequest) {
        validEmpty(lineRequest.getName(), lineRequest.getColor());
        LineResponse response = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    private void validEmpty(String name, String color) {
        if (name.isEmpty() || color.isEmpty()) {
            throw new IllegalArgumentException("노선의 이름과 색은 빈 값일 수 없습니다.");
        }
    }

    @PostMapping("/{line-id}/sections")
    public void saveSection(@PathVariable(value = "line-id") Long lineId,
                            @RequestBody SectionRequest sectionRequest) {
        lineService.saveSection(lineId, sectionRequest);
    }

    @GetMapping
    public List<LineResponse> fineLineAll() {
        return lineService.findLineAll();
    }

    @GetMapping("/{id}")
    public LineResponse findLineById(@PathVariable Long id) {
        return lineService.findLineResponseById(id);
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLineById(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
