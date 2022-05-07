package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@Service
public class SpringLineService implements LineService {

    private final LineDao lineDao;

    public SpringLineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Override
    @Transactional
    public Line save(final LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.save(line);
    }

    @Override
    @Transactional(readOnly = true)
    public Line findById(final Long id) {
        return lineDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Override
    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        lineDao.update(id, lineRequest);
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        lineDao.deleteById(id);
    }
}
