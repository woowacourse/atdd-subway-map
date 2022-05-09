package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line save(final LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.save(line);
    }

    @Transactional(readOnly = true)
    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
