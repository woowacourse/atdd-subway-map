package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.*;
import wooteco.subway.domain.section.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

import java.util.List;
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

        Lines lines = new Lines(lineDao.findAll());
        Line line = lines.save(lineRequest.toLine());
        Line newLine = lineDao.save(line);

        Section section = new Section(newLine.getId(), upStation.getId(), downStation.getId(), lineRequest.getDistance());
        sectionDao.save(section);

        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), createStationResponseOf(newLine));
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
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()), new ConcreteCreationStrategy(), new ConcreteDeletionStrategy(), new ConcreteSortStrategy());
        List<Station> stations = stationDao.findByIdIn(sections.getStationIds());

        List<Station> sortedStations = sections.sort(stations);

        return sortedStations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void edit(Long id, String name, String color) {
        lineDao.edit(id, name, color);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
