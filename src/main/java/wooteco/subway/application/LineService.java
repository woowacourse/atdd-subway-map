package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.constant.DuplicateException;
import wooteco.subway.exception.constant.NotExistException;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line saveAndGet(String name, String color) {
        if (lineDao.existByName(name)) {
            throw new DuplicateException();
        }
        long savedLineId = lineDao.save(new Line(name, color));
        return new Line(savedLineId, name, color);
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(NotExistException::new);
    }

    public Line update(Long id, String name, String color) {
        Line line = findById(id);

        if (isDuplicateName(line, name)) {
            throw new DuplicateException();
        }

        return lineDao.update(new Line(id, name, color));
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineDao.existByName(name);
    }

    public void deleteById(Long id) {
        if(!lineDao.existById(id)) {
            throw new NotExistException();
        }
        lineDao.deleteById(id);
    }
}
