package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.entity.LineEntity;

@Service
@Transactional
public class LineService {

    private static final String DUPLICATE_NAME_ERROR = "이미 같은 이름의 노선이 존재합니다.";
    private static final String NOT_EXIST_ERROR = "해당 노선이 존재하지 않습니다.";

    private final SectionService sectionService;
    private final LineDao lineDao;

    public LineService(SectionService sectionService, LineDao lineDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        LineEntity lineEntity = lineRequest.toLineEntity();
        checkNameDuplication(lineRequest);
        LineEntity savedLineEntity = lineDao.save(lineEntity);
        SectionRequest sectionRequest = new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());

        sectionService.createSection(savedLineEntity.getId(), sectionRequest);
        List<Station> stations = sectionService.getOrderedStations(savedLineEntity.getId());

        Line savedLine = new Line(savedLineEntity.getId(), savedLineEntity.getName(), savedLineEntity.getColor(),
                new Stations(stations));
        return LineResponse.of(savedLine);
    }

    private void checkNameDuplication(LineRequest lineRequest) {
        Optional<LineEntity> wrappedLineEntity = lineDao.findByName(lineRequest.getName());
        if (wrappedLineEntity.isPresent()) {
            throw new DuplicateKeyException(DUPLICATE_NAME_ERROR);
        }
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = getAllLines();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    private List<Line> getAllLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        return lineEntities.stream()
                .map(this::getLine)
                .collect(Collectors.toList());
    }

    private Line getLine(LineEntity lineEntity) {
        List<Station> orderedStations = sectionService.getOrderedStations(lineEntity.getId());
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), new Stations(orderedStations));
    }

    public LineResponse findLineById(Long id) {
        Optional<LineEntity> wrappedLineEntity = lineDao.findById(id);
        checkLineExist(wrappedLineEntity);
        LineEntity lineEntity = wrappedLineEntity.get();
        Line line = getLine(lineEntity);
        return LineResponse.of(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        checkLineExist(lineDao.findById(id));
        LineEntity lineEntity = lineRequest.toLineEntity();
        lineDao.update(id, lineEntity);
    }

    public void deleteLine(Long id) {
        checkLineExist(lineDao.findById(id));
        lineDao.deleteById(id);
    }

    private void checkLineExist(Optional<LineEntity> wrappedLine) {
        if (wrappedLine.isEmpty()) {
            throw new NoSuchElementException(NOT_EXIST_ERROR);
        }
    }

    public void createSection(Long lineId, SectionRequest sectionRequest) {
        checkLineExist(lineDao.findById(lineId));
        sectionService.createSection(lineId, sectionRequest);
    }

    public void deleteSection(Long lineId, Long stationId) {
        checkLineExist(lineDao.findById(lineId));
        sectionService.deleteSection(lineId, stationId);
    }
}
