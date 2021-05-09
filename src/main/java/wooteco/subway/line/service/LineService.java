package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineNameDuplicatedException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineRequest;
import wooteco.subway.line.service.dao.LineDao;

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

        Line line = new Line(name, color);
        return lineDao.save(line);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findLineById(Long id) {
        return lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
    }

    public Optional<Line> findLineByName(String name) {
        return lineDao.findLineByName(name);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void removeLine(Long id) {
        lineDao.removeLine(id);
    }
}
