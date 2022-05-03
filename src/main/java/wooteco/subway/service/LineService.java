package wooteco.subway.service;

import java.util.List;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(final Line line) {
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long lineId) {
        return lineDao.findById(lineId);
    }

    public void update(final Line line) {
        lineDao.update(line);
    }

    public void delete(final Long lineId) {
        lineDao.delete(lineId);
    }
}
