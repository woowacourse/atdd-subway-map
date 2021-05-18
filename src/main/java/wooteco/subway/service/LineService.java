package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse addLine(LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        SectionRequest sectionRequest = lineRequest.createSectionRequest();

        Long lineId = lineDao.insert(line);
        sectionService.createSection(lineId, sectionRequest);
        return new LineResponse(lineId, line);
    }

    @Transactional
    public List<LineResponse> findLines() {
        List<Line> lines = lineDao.selectAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLineWithStations(Long id) {
        Line line = lineDao.select(id);
        List<StationResponse> stationResponses = getStationsInLine(id).stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return new LineResponse(line, stationResponses);
    }

    private List<Station> getStationsInLine(Long id) {
        Sections sections = sectionService.loadSections(id);
        return sections.getStations();
    }

    @Transactional
    public void modifyLine(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.createLine());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineDao.delete(id);
    }
}

