package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SubwayService;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final SubwayService subwayService;

    public SectionController(SubwayService subwayService) {
        this.subwayService = subwayService;
    }

    @PostMapping("/{id}/sections")
    @ResponseStatus(HttpStatus.OK)
    public void createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        subwayService.addSection(id, sectionRequest);
    }
}
