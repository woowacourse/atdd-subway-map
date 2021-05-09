package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineNameDuplicatedException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.dao.LineDao;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(String name, String color) {
        if (lineDao.findLineByName(name).isPresent()) {
            throw new LineNameDuplicatedException();
        }
        Line line = Line.of(name, color);
        return lineDao.save(line);
    }

    public List<Line> showLines() {
        return lineDao.findAll();
    }

    public Line findLineById(Long id) {
        return lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
    }

    public void updateLine(Long id, String name, String color) {
        final Optional<Line> lineByName = lineDao.findLineByName(name);
        if (lineByName.isPresent() && lineByName.get().isNotSameId(id)) {
            throw new LineNameDuplicatedException();
        }
        lineDao.update(id, name, color);
    }

    public void removeById(Long id) {
        lineDao.removeById(id);
    }

}
