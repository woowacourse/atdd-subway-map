package wooteco.subway.service;


import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao jdbcLineDao) {
        this.lineDao = jdbcLineDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        validateRequest(newLine);

        Long lineId = lineDao.save(newLine);
        return createLineResponse(lineDao.findById(lineId));
    }

    private void validateRequest(Line line) {
        validateName(line);
        validateColor(line);
    }

    private void validateName(Line line) {
        if (lineDao.existByName(line)) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
    }

    private void validateColor(Line line) {
        if (lineDao.existByColor(line)) {
            throw new IllegalArgumentException("이미 존재하는 노선 색깔입니다.");
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse findById(Long lineId) {
        Line line = lineDao.findById(lineId);
        return createLineResponse(line);
    }

    public void delete(Long lineId) {
        lineDao.deleteById(lineId);
    }

    public void update(Long lineId, LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(lineId, newLine);
    }

    private LineResponse createLineResponse(Line newLine) {
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }
}
