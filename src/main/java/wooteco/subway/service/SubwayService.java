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
import wooteco.subway.domain.SectionAddManager;
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
        SectionRequest sectionRequest = toSectionRequest(lineRequest);
        saveSection(line.getId(), sectionRequest);
        return makeLineResponse(line);
    }

    private SectionRequest toSectionRequest(LineRequest lineRequest) {
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
    }

    private void saveSection(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRequest.toEntity(lineId);
        sectionDao.save(section);
    }

    private LineResponse makeLineResponse(Line line) {
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

    private Line saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        Lines lines = new Lines(lineDao.findAll());
        lines.checkAbleToAdd(line);
        return lineDao.save(line);
    }

    public List<LineResponse> getLines() {
        return lineDao.findAll()
                .stream()
                .map(this::makeLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineDao.findById(id);
        return makeLineResponse(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRequest.toEntity(id);
        lineDao.update(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRequest.toEntity();
        Stations stations = new Stations(stationDao.findAll());
        stations.checkAbleToAdd(station);
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

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        SectionAddManager addManager = SectionAddManager.of(sectionDao.findByLineId(lineId));
        List<Section> updatedSections = addManager.add(sectionRequest.toEntity(lineId));
        sectionDao.delete(lineId);
        updatedSections.forEach(sectionDao::save);
    }
}
