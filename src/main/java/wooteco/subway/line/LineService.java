package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

@Service
public class LineService {

    @Autowired
    private LineDao lineDao;

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(),
            newLine.getColor(), null);
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines
            .stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), null))
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
            null);
    }

    public void updateById(Long id, LineRequest lineRequest) {
        Line persistedLine = lineDao.findById(id);
        Line updatedLine = new Line(persistedLine.getId(), lineRequest.getName(),
            lineRequest.getColor());
        lineDao.update(updatedLine);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
