package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.line.LineNameDuplicatedException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.dao.LineDao;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line create(String name, String color) {
        if (lineDao.findByName(name).isPresent()) {
            throw new LineNameDuplicatedException();
        }
        Line line = Line.of(name, color);
        return lineDao.save(line);
    }

    public List<Line> showAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id).orElseThrow(LineNotFoundException::new);
    }

    public void update(Long id, String name, String color) {
        final Optional<Line> lineByName = lineDao.findByName(name);
        if (lineByName.isPresent() && lineByName.get().isNotSameId(id)) {
            throw new LineNameDuplicatedException();
        }
        lineDao.update(id, name, color);
    }

    public void removeById(Long id) {
        lineDao.removeById(id);
    }

}
