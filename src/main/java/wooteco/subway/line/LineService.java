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
        validateDuplication(lineRequest.getName());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.save(line);
    }

    private void validateDuplication(String name) {
        if (isDuplicated(name)) {
            throw new LineDuplicationException();
        }
    }

    private boolean isDuplicated(String name) {
        return lineDao.findByName(name).isPresent();
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
