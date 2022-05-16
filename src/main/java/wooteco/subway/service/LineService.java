package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;
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

        Sections sections = sectionService.createSection(savedLineEntity.getId(), sectionRequest);

        Line savedLine = new Line(savedLineEntity.getId(), savedLineEntity.getName(), savedLineEntity.getColor(),
                sections);
        return LineResponse.of(savedLine);
    }

    private void checkNameDuplication(LineRequest lineRequest) {
        if (lineDao.findByName(lineRequest.getName()).isPresent()) {
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
        Sections sections = sectionService.getSectionsByLineId(lineEntity.getId());
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), sections);
    }

    public LineResponse findLineById(Long id) {
        LineEntity lineEntity = getLineEntity(id);
        Line line = getLine(lineEntity);
        return LineResponse.of(line);
    }

    private LineEntity getLineEntity(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NOT_EXIST_ERROR));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        getLineEntity(id);
        LineEntity lineEntity = lineRequest.toLineEntity();
        lineDao.update(id, lineEntity);
    }

    public void deleteLine(Long id) {
        getLineEntity(id);
        lineDao.deleteById(id);
    }

    public void createSection(Long lineId, SectionRequest sectionRequest) {
        getLineEntity(lineId);
        sectionService.createSection(lineId, sectionRequest);
    }

    public void deleteSection(Long lineId, Long stationId) {
        getLineEntity(lineId);
        sectionService.deleteSection(lineId, stationId);
    }
}
