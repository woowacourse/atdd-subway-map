package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.NoLineException;

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
        return lineDao.findById(id)
            .orElseThrow(NoLineException::new);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
