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

    public SpringLineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Override
    public Line save(LineRequest lineRequest) {
        Line line = new Line(null, lineRequest.getName(), lineRequest.getColor());
        return lineDao.save(line);
    }

    @Override
    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    @Override
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Override
    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest);
    }

    @Override
    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
