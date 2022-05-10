package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Subway;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class SubwayService {

    private Subway subway;
    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    public SubwayService(Subway subway, LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.subway = subway;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = Station.of(stationRequest.getName());
        subway.checkAbleToAdd(stationDao.findAll(), station);
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation);
    }

    public List<StationResponse> getStations() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }

    public LineResponse addLine(LineRequest lineRequest) {
        Line line = Line.of(lineRequest.getName(), lineRequest.getColor());
        subway.checkAbleToAdd(lineDao.findAll(), line);
        Line newLine = lineDao.save(line);
        saveSection(newLine.getId(), lineRequest);
        return toLineResponse(newLine);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = Line.of(id, lineRequest.getName(), lineRequest.getColor());
        subway.checkAbleToAdd(lineDao.findAll(), line);
        lineDao.update(line);
    }

    public List<LineResponse> getLines() {
        return lineDao.findAll()
                .stream()
                .map(this::toLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineDao.findById(id);
        return toLineResponse(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Section newSection = Section.of(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        List<Section> sections = subway.addSection(sectionDao.findByLineId(lineId), newSection);
        sectionDao.delete(lineId);
        sectionDao.saveAll(sections);
    }

    public void deleteSection(Long lineId, Long stationId) {
        List<Section> sections = subway.deleteSection(sectionDao.findByLineId(lineId), stationId);
        sectionDao.delete(lineId);
        sectionDao.saveAll(sections);
    }

    private void saveSection(Long lineId, LineRequest lineRequest) {
        SectionRequest sectionRequest = toSectionRequest(lineRequest);
        Section section = sectionRequest.toEntity(lineId);
        sectionDao.save(section);
    }

    private List<StationResponse> makeStationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private LineResponse toLineResponse(Line line) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        List<Station> stations = sections.extractStationIds()
                .stream()
                .map(stationDao::findById)
                .collect(Collectors.toList());
        return new LineResponse(line, makeStationResponses(stations));
    }

    private SectionRequest toSectionRequest(LineRequest lineRequest) {
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
    }
}
