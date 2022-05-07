package wooteco.subway.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
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

    private LineResponse makeLineResponse(Line line) {
        Set<Station> stations = getLineStations(line);
        return new LineResponse(line, makeStationResponses(stations));
    }

    private List<StationResponse> makeStationResponses(Set<Station> stations) {
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private SectionRequest toSectionRequest(LineRequest lineRequest) {
        return new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
    }

    private Set<Station> getLineStations(Line line) {
        List<Section> sections = sectionDao.findByLineId(line.getId());
        return toStations(sections);
    }

    private Set<Station> toStations(List<Section> sections) {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(stationDao.findById(section.getUpStationId()));
            stations.add(stationDao.findById(section.getDownStationId()));
        }
        return stations;
    }


    private void saveSection(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRequest.toEntity(lineId);
        sectionDao.save(section);
    }

    private Line saveLine(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        return lineDao.save(line);
    }

    public List<LineResponse> getLines() {
        return lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line);
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
}
