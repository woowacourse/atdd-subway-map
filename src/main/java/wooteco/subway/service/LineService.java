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
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
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

    public LineResponse save(LineRequest lineRequest) {
        Line savedLine = saveLine(lineRequest);
        saveSection(lineRequest, savedLine.getId());
        List<StationResponse> stations =
                findStations(lineRequest.getUpStationId(), lineRequest.getDownStationId());
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(), stations);
    }

    private Line saveLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateLineName(line);
        return lineDao.save(line);
    }

    private void validateLineName(Line line) {
        if (lineDao.exists(line)) {
            throw new DuplicatedLineNameException();
        }
    }

    private void saveSection(LineRequest lineRequest, Long lineId) {
        Section section = new Section(lineId, lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionService.saveInitSection(section);
    }

    private List<StationResponse> findStations(Long upStationId, Long downStationId) {
        return stationService.findByIds(Arrays.asList(upStationId, downStationId));
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(it -> findLineById(it.getId()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }

    public LineResponse findLineById(Long id) {
        validateId(id);
        Line line = lineDao.findById(id);
        Sections sections = new Sections(sectionService.findByLineId(id));
        List<Long> stationIds = sections.getAllStationIds();
        List<StationResponse> stations = stationService.findByIds(stationIds);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public void update(Long id, LineRequest lineRequest) {
        validateId(id);
        Line updatingLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(id, updatingLine);
    }

    private void validateId(Long id) {
        if (!lineDao.exists(id)) {
            throw new InvalidLineIdException();
        }
    }
}
