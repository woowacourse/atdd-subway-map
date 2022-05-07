package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse save(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void changeLineName(Long id, String name) {
        validateId(id);
        lineDao.changeLineName(id, name);
    }

    private void validateId(Long id) {
        if (!lineDao.isValidId(id)) {
            throw new EmptyResultDataAccessException(id.intValue());
        }
    }

    public void deleteById(Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }
}
