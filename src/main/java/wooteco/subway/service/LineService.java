package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Station downStation = stationDao.findById(lineRequest.getDownStationId());
        Station upStation = stationDao.findById(lineRequest.getUpStationId());

        Line line = saveLine(lineRequest.toLine());

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), lineRequest.getDistance());
        sectionDao.save(section);

        return new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponseOf(line));
    }

    private Line saveLine(Line line) {
        if(lineDao.existByName(line.getName())){
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
        return lineDao.save(line);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponseOf(line)))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponseOf(line));
    }

    private List<StationResponse> createStationResponseOf(Line line) {
        List<Station> stations = findStationsIn(line);

        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    private List<Station> findStationsIn(Line line) {
        List<Section> sections = sectionDao.findSectionsIn(line);

        Set<Long> stationIds = new HashSet<>();
        for (Section section : sections) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        return stationDao.findByIdIn(stationIds);
    }

    public void edit(Long id, String name, String color) {
        lineDao.edit(id, name, color);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
