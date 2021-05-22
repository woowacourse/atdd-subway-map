package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotExistItemException;
import wooteco.subway.line.dto.LineOnlyDataResponse;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.SectionService;
import wooteco.subway.line.section.Sections;
import wooteco.subway.line.section.dto.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;
import wooteco.subway.station.Stations;
import wooteco.subway.station.dto.StationResponse;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, StationService stationService,
        SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);

        SectionRequest sectionRequest = new SectionRequest(lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionService.save(newLine.getId(), sectionRequest, true);

        List<StationResponse> stationResponses = getSortedStationResponses(newLine);
        return new LineResponse(newLine, stationResponses);
    }

    private List<StationResponse> getSortedStationResponses(Line newLine) {
        List<Section> lineSections = sectionService.findByLineId(newLine.getId());
        Sections sections = sectionService.getSortedSections(lineSections);

        List<Station> lineStations = stationService.findByLineId(newLine.getId());
        Stations stations = new Stations(lineStations);
        stations.sort(sections);
        return stations.stream().map(StationResponse::new).collect(
            Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        List<StationResponse> stationResponses = getSortedStationResponses(line);
        return new LineResponse(line, stationResponses);
    }

    public List<LineOnlyDataResponse> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
            .map(LineOnlyDataResponse::new).collect(Collectors.toList());
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());

        validate(lineDao.update(line));
    }

    private void validate(int updateRow) {
        if (updateRow != 1) {
            throw new NotExistItemException();
        }
    }

    public void delete(Long id) {
        sectionService.deleteByLineId(id);
        lineDao.delete(id);
    }
}
