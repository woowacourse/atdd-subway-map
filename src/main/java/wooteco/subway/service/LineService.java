package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {
    static final String DUPLICATE_EXCEPTION_MESSAGE = "이름이나 색깔이 중복된 노선은 만들 수 없습니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public Line create(Line line) {
        try {
            return lineDao.save(line);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public List<Line> showAll() {
        return lineDao.findAll();
    }

    public Line show(Long id) {
        return lineDao.findById(id);
    }

    @Transactional
    public void update(Line line) {
        try {
            lineDao.update(line);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
