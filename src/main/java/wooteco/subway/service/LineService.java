package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.DuplicatedLineException;
import wooteco.subway.exception.line.LineNotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        if (lineDao.exists(line)) {
            throw new DuplicatedLineException();
        }
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void deleteById(Long id) {
        int executionResult = lineDao.deleteById(id);
        if (executionResult == 0) {
            throw new LineNotFoundException();
        }
    }

    public Line findLineById(Long id) {
        Optional<Line> line = lineDao.findById(id);
        validateNull(line);
        return line.get();
    }

    public void update(Long id, Line updatingLine) {
        int executionResult = lineDao.update(id, updatingLine);
        if (executionResult == 0) {
            throw new LineNotFoundException();
        }
    }

    private void validateNull(Optional<Line> line) {
        if (line.isEmpty()) {
            throw new LineNotFoundException();
        }
    }
}
