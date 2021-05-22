package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.dto.LineOnlyDataResponse;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.SectionDao;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.Stations;
import wooteco.subway.station.dto.StationResponse;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);

        saveLineInSection(lineRequest, newLine);

        List<StationResponse> stationResponses = getSortedStationResponses(newLine);
        return new LineResponse(newLine, stationResponses);
    }

    private void saveLineInSection(LineRequest lineRequest, Line newLine) {
        Section section = new Section(lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());

        if (!stationDao.isExistStations(section.getUpStationId(), section.getDownStationId())) {
            throw new SubwayCustomException(SubwayException.NOT_EXIST_STATION_EXCEPTION);
        }
        sectionDao.save(newLine.getId(), section);
    }

    private List<StationResponse> getSortedStationResponses(Line newLine) {
        Sections sections = getSortedSections(newLine);

        List<Station> lineStations = stationDao.findByLineId(newLine.getId());
        Stations stations = new Stations(lineStations);
        stations.sort(sections);
        return stations.stream().map(StationResponse::new).collect(
            Collectors.toList());
    }

    private Sections getSortedSections(Line newLine) {
        List<Section> lineSections = sectionDao.findByLineId(newLine.getId());
        Sections sections = new Sections(lineSections);
        sections.sort();
        return sections;
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
            throw new SubwayCustomException(SubwayException.NOT_EXIST_LINE_EXCEPTION);
        }
    }

    public void delete(Long id) {
        sectionDao.deleteByLineId(id);
        lineDao.delete(id);
    }
}
