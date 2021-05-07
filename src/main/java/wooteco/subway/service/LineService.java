package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.common.exception.SubwayHttpException;
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
            // todo 예외 나누기?
            throw new SubwayHttpException("중복된 이름입니다");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    private Line findById(Long id) {
        try {
            return lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new SubwayHttpException(HttpStatus.NOT_FOUND, "존재하지 않는 노선입니다");
        }
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
