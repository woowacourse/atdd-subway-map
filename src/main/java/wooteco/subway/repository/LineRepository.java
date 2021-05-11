package wooteco.subway.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.domain.line.Line;

@Repository
public class LineRepository {

    private final LineDao lineDao;

    public LineRepository(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        return lineDao.save(line);
    }

    public boolean existsByName(String name) {
        return lineDao.existsByName(name);
    }

    public boolean existsById(Long id) {
        return lineDao.existsById(id);
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선입니다."));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
