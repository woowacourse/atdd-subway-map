package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.repository.LineRepository;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse addLine(LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        SectionRequest sectionRequest = lineRequest.createSectionRequest();

        Long lineId = lineRepository.insert(line);
        sectionService.createSection(lineId, sectionRequest);
        return new LineResponse(lineId, line);
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.selectAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLineWithStations(Long id) {
        Line line = lineRepository.select(id);
        List<StationResponse> stationResponses = getStationsInLine(id).stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return new LineResponse(line, stationResponses);
    }

    private List<Station> getStationsInLine(Long id) {
        Sections sections = sectionService.loadSections(id);
        return sections.getStations();
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        lineRepository.update(id, lineRequest.createLine());
    }

    public void deleteLine(Long id) {
        lineRepository.delete(id);
    }
}
