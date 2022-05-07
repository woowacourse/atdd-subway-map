package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
        try {
            return lineDao.findById(id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new LineNotFoundException();
        }
    }

    public void update(Line updatingLine) {
        int executionResult = lineDao.update(updatingLine);
        if (executionResult == 0) {
            throw new LineNotFoundException();
        }
    }
}
