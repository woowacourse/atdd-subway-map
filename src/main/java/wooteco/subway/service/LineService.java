package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line save(final Line line) {
        if (lineDao.existByName(line.getName())) {
            throw new IllegalStateException("이미 존재하는 노선 이름입니다.");
        }
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
