package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.web.exception.NotFoundException;
import wooteco.subway.web.exception.SubwayHttpException;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.dao.LineDao;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line add(Line line) {
        Long id = addLine(line);
        return findById(id);
    }

    private Long addLine(Line line) {
        try {
            return lineDao.save(line);
        } catch (DuplicateKeyException e) {
            throw new SubwayHttpException("중복된 노선 이름입니다");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void update(Long id, Line line) {
        findById(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        findById(id);
        lineDao.delete(id);
    }

    public Line findById(Long id) {
        try {
            return lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 노선입니다");
        }
    }
}
