package wooteco.subway.line.repository;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;

import java.util.List;

public class LineRepositoryImpl implements LineRepository {
    private final static LineRepositoryImpl instance = new LineRepositoryImpl();

    private final LineDao lineDao = new LineDao();

    private LineRepositoryImpl() {
    }

    public static LineRepository getInstance() {
        return instance;
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

}
