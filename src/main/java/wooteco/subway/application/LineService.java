package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistException;

@Service
@Transactional
public class LineService {

    public Line save(String name, String color) {
        if (LineDao.existByName(name)) {
            throw new DuplicateException();
        }
        return LineDao.save(new Line(name, color));
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return LineDao.findById(id)
            .orElseThrow(NotExistException::new);
    }

    public Line update(Long id, String name, String color) {
        Line line = findById(id);

        if (isDuplicateName(line, name)) {
            throw new DuplicateException();
        }

        return LineDao.update(new Line(id, name, color));
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && LineDao.existByName(name);
    }

    public void deleteById(Long id) {
        if(!LineDao.existById(id)) {
            throw new NotExistException();
        }
        LineDao.deleteById(id);
    }
}
