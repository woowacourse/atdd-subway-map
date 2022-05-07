package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        validateDuplicatedName(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return toLineBasicResponse(lineDao.save(line));
    }

    private void validateDuplicatedName(LineRequest lineRequest) {
        if (lineDao.existByName(lineRequest.getName())) {
            throw new IllegalArgumentException("중복되는 이름의 지하철 노선이 존재합니다.");
        }
    }

    private LineResponse toLineBasicResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll().stream()
            .map(this::toLineBasicResponse)
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        if (!lineDao.existById(id)) {
            throw new NotFoundException("해당되는 노선은 존재하지 않습니다.");
        }
        return toLineBasicResponse(lineDao.findById(id));

    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        validateById(id);
        validateDuplicatedName(lineRequest);
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
    }

    private void validateById(Long id) {
        if (!lineDao.existById(id)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
    }

    @Transactional
    public void delete(Long id) {
        validateById(id);
        lineDao.deleteById(id);
    }
}
