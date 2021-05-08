package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.IllegalIdException;
import wooteco.subway.exception.line.LineDuplicationException;
import wooteco.subway.exception.line.NoLineException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineRequest;

import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;

    private LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line add(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicatedName(line.getName());
        validateDuplicatedColor(line.getColor());
        return lineDao.save(line);
    }

    private void validateDuplicatedName(String name) {
        lineDao.findByName(name)
            .ifPresent(this::throwDuplicationException);
    }

    private void validateDuplicatedColor(String color) {
        lineDao.findByColor(color)
            .ifPresent(this::throwDuplicationException);
    }

    private void throwDuplicationException(Line line) {
        throw new LineDuplicationException();
    }

    public List<Line> lines() {
        return lineDao.findAll();
    }

    public Line line(Long id) {
        validateId(id);
        return lineDao.findById(id)
            .orElseThrow(NoLineException::new);
    }

    public void update(Long id, LineRequest lineRequest) {
        validateId(id);
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void delete(Long id) {
        validateId(id);
        lineDao.delete(id);
    }

    private void validateId(Long id) {
        if (id <= 0) {
            throw new IllegalIdException();
        }
    }
}
