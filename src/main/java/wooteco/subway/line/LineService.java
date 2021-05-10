package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest);
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine);
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();

        return lines.stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }

    public void update(Long id, LineRequest lineRequest) {
        validate(id);

        Line line = new Line(id, lineRequest);
        lineDao.update(line);
    }

    private void validate(Long id) {
        findById(id);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
