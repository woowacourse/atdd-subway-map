package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.dao.LineDao;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line add(Line line) {
        // todo 예외 캐치
        Long id = lineDao.save(line);
        return lineDao.findById(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
