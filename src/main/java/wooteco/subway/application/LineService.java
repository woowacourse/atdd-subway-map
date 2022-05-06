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

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(String name, String color) {
        if (lineDao.existByName(name)) {
            throw new DuplicateException(String.format("%s는 중복된 노선 이름입니다.", name));
        }
        return lineDao.save(new Line(name, color));
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new NotExistException(String.format("%d와 동일한 ID의 노선이 없습니다.", id)));
    }

    public Line update(Long id, String name, String color) {
        Line line = findById(id);

        if (isDuplicateName(line, name)) {
            throw new DuplicateException(String.format("%s는 중복된 노선 이름입니다.", name));
        }

        return lineDao.update(new Line(id, name, color));
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineDao.existByName(name);
    }

    public void deleteById(Long id) {
        if (!lineDao.existById(id)) {
            throw new NotExistException(String.format("%d와 동일한 ID의 노선이 없습니다.", id));
        }
        lineDao.deleteById(id);
    }
}
