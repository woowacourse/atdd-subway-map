package wooteco.subway.domain.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

import java.util.List;

@Repository
public class LineRepository {
    private final LineDao lineDao;

    public LineRepository(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Long insert(Line line) {
        return lineDao.insert(line);
    }

    public List<Line> selectAll() {
        return lineDao.selectAll();
    }

    public Line select(Long id) {
        return lineDao.select(id);
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
