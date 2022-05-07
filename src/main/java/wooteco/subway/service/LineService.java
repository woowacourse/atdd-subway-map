package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.DuplicatedLineException;
import wooteco.subway.exception.line.LineNotFoundException;

@Service
public class LineService {

    private static final int NONE = 0;

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        if (lineDao.existsByNameOrColor(line)) {
            throw new DuplicatedLineException();
        }
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void deleteById(Long id) {
        int executedRows = lineDao.deleteById(id);
        if (executedRows == NONE) {
            throw new LineNotFoundException();
        }
    }

    public Line findLineById(Long id) {
        try {
            return lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new LineNotFoundException();
        }
    }

    public void update(Line updatingLine) {
        int executedRows = lineDao.update(updatingLine);
        if (executedRows == NONE) {
            throw new LineNotFoundException();
        }
    }
}
