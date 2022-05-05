package wooteco.subway.service;

import java.util.List;
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
        validateId(id);
        lineDao.deleteById(id);
    }

    public Line findLineById(Long id) {
        validateId(id);
        return lineDao.findById(id);
    }

    public void update(Long id, Line updatingLine) {
        validateId(id);
        lineDao.update(id, updatingLine);
    }

    private void validateId(Long id) {
        if (!lineDao.exists(id)) {
            throw new LineNotFoundException();
        }
    }
}
