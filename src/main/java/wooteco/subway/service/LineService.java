package wooteco.subway.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.StationDto;
import wooteco.subway.exception.line.DuplicatedLineNameException;
import wooteco.subway.exception.line.InvalidLineIdException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationService stationService;
    private final SectionService sectionService;

    @Autowired
    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public LineDto save(String name, String color, Long upStationId, Long downStationId, int distance) {
        Line savedLine = saveLine(name, color);
        sectionService.saveInitSection(new Section(savedLine.getId(), upStationId, downStationId, distance));
        List<StationDto> stations = findStations(upStationId, downStationId);
        return new LineDto(savedLine, stations);
    }

    private Line saveLine(String name, String color) {
        Line line = new Line(name, color);
        validateLineName(line);
        return lineDao.save(line);
    }

    private void validateLineName(Line line) {
        if (lineDao.exists(line)) {
            throw new DuplicatedLineNameException();
        }
    }

    private List<StationDto> findStations(Long upStationId, Long downStationId) {
        return stationService.findByIds(Arrays.asList(upStationId, downStationId));
    }

    public List<LineDto> findAll() {
        return lineDao.findAll()
                .stream()
                .map(it -> findLineById(it.getId()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }

    public LineDto findLineById(Long id) {
        validateId(id);
        Line line = lineDao.findById(id);
        Sections sections = new Sections(sectionService.findByLineId(id));
        List<Long> stationIds = sections.getAllStationIds();
        List<StationDto> stations = stationService.findByIds(stationIds);
        return new LineDto(line, stations);
    }

    public void update(Long id, String name, String color) {
        validateId(id);
        Line updatingLine = new Line(name, color);
        lineDao.update(id, updatingLine);
    }

    private void validateId(Long id) {
        if (!lineDao.exists(id)) {
            throw new InvalidLineIdException();
        }
    }
}
