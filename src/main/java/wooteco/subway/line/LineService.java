package wooteco.subway.line;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(String color, String name) {
        return new LineResponse(lineDao.insert(color, name));
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 노선이 존재하지 않습니다."));
        return new LineResponse(line);
    }

    public void updateById(Long id, String color, String name) {
        lineDao.update(id, color, name);
    }

    public void deleteById(Long id) {
        lineDao.delete(id);
    }
}
