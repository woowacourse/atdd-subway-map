package wooteco.subway.service;


import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicateLineException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        validateCreateRequest(newLine);

        Long lineId = lineDao.save(newLine);

        return createLineResponse(lineDao.findById(lineId));
    }

    private void validateCreateRequest(Line line) {
        validateName(line);
        validateColor(line);
    }

    private void validateName(Line line) {
        if (lineDao.existByName(line)) {
            throw new DuplicateLineException("이미 존재하는 노선 이름입니다.");
        }
    }

    private void validateColor(Line line) {
        if (lineDao.existByColor(line)) {
            throw new DuplicateLineException("이미 존재하는 노선 색깔입니다.");
        }
    }

    private LineResponse createLineResponse(Line newLine) {
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
            .map(this::createLineResponse)
            .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long lineId) {
        Line line = lineDao.findById(lineId);
        return createLineResponse(line);
    }

    public void update(Long lineId, LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        validateUpdateRequest(lineId, newLine);

        lineDao.update(lineId, newLine);
    }

    private void validateUpdateRequest(Long lineId, Line line) {
        validateNameExceptSameId(lineId, line);
        validateColorExceptSameId(lineId, line);
    }

    private void validateNameExceptSameId(Long lineId, Line line) {
        if (lineDao.existByNameExceptSameId(lineId, line)) {
            throw new DuplicateLineException("이미 존재하는 노선 이름으로 업데이트할 수 없습니다.");
        }
    }

    private void validateColorExceptSameId(Long lineId, Line line) {
        if (lineDao.existByColorExceptSameId(lineId, line)) {
            throw new DuplicateLineException("이미 존재하는 노선 색깔로 업데이트할 수 없습니다.");
        }
    }

    public void delete(Long lineId) {
        lineDao.deleteById(lineId);
    }
}
