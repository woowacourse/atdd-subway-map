package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        if (lineDao.hasLine(lineRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
        final Long newLineId = lineDao.save(line);
        return new LineResponse(newLineId, line.getName(), line.getColor());
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        final Line line = checkExistLine(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void updateLine(Long id, String name, String color) {
        checkExistLine(id);
        lineDao.updateById(id, name, color);
    }

    public void deleteLine(Long id) {
        checkExistLine(id);
        lineDao.deleteById(id);
    }

    private Line checkExistLine(Long id) {
        final Line line = lineDao.findById(id);
        if (line == null) {
            throw new IllegalArgumentException("해당하는 노선이 존재하지 않습니다.");
        }
        return line;
    }
}
