package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.NoLineException;

import java.util.List;
import java.util.Optional;

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
        Optional.ofNullable(lineDao.findByName(name))
            .orElseThrow(LineDuplicationException::new);
    }

    private void validateDuplicatedColor(String color) {
        Optional.ofNullable(lineDao.findByColor(color))
            .orElseThrow(LineDuplicationException::new);
    }

    public List<Line> lines() {
        return lineDao.findAll();
    }

    public Line line(Long id){
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
