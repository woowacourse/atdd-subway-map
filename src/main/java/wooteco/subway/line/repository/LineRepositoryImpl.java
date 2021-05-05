package wooteco.subway.line.repository;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;

import java.util.List;

public class LineRepositoryImpl implements LineRepository {

    private final LineDao lineDao;

    public LineRepositoryImpl(final LineDao lineDao) {
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

}
