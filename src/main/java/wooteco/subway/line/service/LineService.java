package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;

import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        if (lineDao.findByName(line.getName()).isPresent()) {
            throw new DuplicatedNameException();
        }
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id).orElseThrow(NotFoundLineException::new);
    }

    public void delete(Long id) {
        lineDao.findById(id).orElseThrow(NotFoundLineException::new);
        lineDao.delete(id);
    }

    public void update(Line line, long id) {
        lineDao.findById(id).orElseThrow(NotFoundLineException::new);
        lineDao.update(line, id);
    }
}
