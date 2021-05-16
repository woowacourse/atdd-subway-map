package wooteco.subway.facade;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SortedStations;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;
import wooteco.subway.web.dto.LineRequest;
import wooteco.subway.web.dto.LineResponse;
import wooteco.subway.web.dto.StationResponse;

@Service
@Transactional
public class LineFacade {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineFacade(LineService lineService,
            StationService stationService,
            SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public LineResponse findById(Long id) {
        Line line = lineService.findLineById(id);

        List<Section> sections = sectionService.findSectionsByLineId(line.getId());
        List<StationResponse> stations = stationService.findStationsBySections(sections);

        List<StationResponse> sortedStations = new SortedStations(sections, stations).get();

        return new LineResponse(line, sortedStations);
    }

    public List<Line> findAll() {
        return lineService.findAll();
    }

    public Long add(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        Section section = lineRequest.toSectionRequest().toEntity();

        Long lineId = lineService.addLine(line);
        sectionService.addSection(lineId, section);
        return lineId;
    }

    public void update(Long id, Line line) {
        lineService.validateLineId(id);
        lineService.updateLine(id, line);
    }

    public void delete(Long id) {
        lineService.validateLineId(id);
        lineService.deleteLine(id);
    }
}
