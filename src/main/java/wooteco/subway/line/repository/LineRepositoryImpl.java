package wooteco.subway.line.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;

import java.util.List;

@Repository
public class LineRepositoryImpl implements LineRepository {
    private final LineDao lineDao;

    public LineRepositoryImpl(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Override
    public long save(Line line) {
        return lineDao.save(line);
    }

    @Override
    public List<Line> allLines() {
        return lineDao.allLines();
    }

    @Override
    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    @Override
    public void clear() {
        lineDao.clear();
    }

    @Override
    public void update(Line line) {
        lineDao.update(line);
    }

    @Override
    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }

}
