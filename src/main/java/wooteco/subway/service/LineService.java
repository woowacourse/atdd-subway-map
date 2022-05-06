package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        try {
            return lineDao.save(line);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        try {
            return lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("존재하지 않는 노선입니다", 1);
        }
    }

    public Line update(Long id, Line line) {
        try {
            return lineDao.update(id, line);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("이미 존재하는 노선 이름입니다.");
        }
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
