package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Lines;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class SubwayService {

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    public SubwayService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse addLine(LineRequest lineRequest) {
        Line line = saveLine(lineRequest);
        saveSection(line.getId(), lineRequest);
        return toLineResponse(line);
    }

    private Line saveLine(LineRequest lineRequest) {
        Line line = convertToLine(lineRequest);
        return lineDao.save(line);
    }

    private void saveSection(Long lineId, LineRequest lineRequest) {
        SectionRequest sectionRequest = toSectionRequest(lineRequest);
        Section section = sectionRequest.toEntity(lineId);
        sectionDao.save(section);
    }

    private SectionRequest toSectionRequest(LineRequest lineRequest) {
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
    }

    private LineResponse toLineResponse(Line line) {
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));
        List<Station> stations = sections.extractStationIds()
                .stream()
                .map(stationDao::findById)
                .collect(Collectors.toList());
        return new LineResponse(line, makeStationResponses(stations));
    }

    private List<StationResponse> makeStationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
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

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = convertToLine(id, lineRequest);
        lineDao.update(line);
    }

    private Line convertToLine(LineRequest lineRequest) {
        return convertToLine(null, lineRequest);
    }

    private Line convertToLine(Long id, LineRequest lineRequest) {
        Line line = Line.of(id, lineRequest.getName(), lineRequest.getColor());
        Lines lines = new Lines(lineDao.findAll());
        lines.checkAbleToAdd(line);
        return line;
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = convertToStation(stationRequest);
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation);
    }

    private Station convertToStation(StationRequest stationRequest) {
        Station station = Station.of(stationRequest.getName());
        Stations stations = new Stations(stationDao.findAll());
        stations.checkAbleToAdd(station);
        return station;
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

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.add(sectionRequest.toEntity(lineId));
        updateSections(lineId, sections);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.delete(stationId);
        updateSections(lineId, sections);
    }

    private void updateSections(Long lineId, Sections sections) {
        sectionDao.delete(lineId);
        sections.forEach(sectionDao::save);
    }
}
