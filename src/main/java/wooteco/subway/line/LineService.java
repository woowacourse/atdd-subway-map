package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;

import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;

    private LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line add(LineRequest lineRequest) {
        List<Line> lines = lineDao.findAll();
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicatedLine(lines, line);
        return lineDao.save(line);
    }

    private void validateDuplicatedLine(List<Line> lines, Line newLine) {
        if (isDuplicatedColor(lines, newLine)) {
            throw new LineDuplicationException();
        }
    }

    private boolean isDuplicatedColor(List<Line> lines, Line newLine) {
        return lines.stream()
                .anyMatch(line -> line.isSameColor(newLine));
    }

    public List<Line> lines() {
        return lineDao.findAll();
    }

    public Line line(Long id) {
        return lineDao.findById(id);
    }

    public void update(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
