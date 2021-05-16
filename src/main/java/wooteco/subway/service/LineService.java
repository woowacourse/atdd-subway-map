package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineDao;
import wooteco.subway.web.exception.NotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line findLine(Long id) {
        try {
            return lineDao.findById(id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("노선이 존재하지 않습니다");
        }
    }

    public Long addLine(Line line) {
        return lineDao.save(line);
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }
}
