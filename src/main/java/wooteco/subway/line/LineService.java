package wooteco.subway.line;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    public LineResponse create(String color, String name) {
        if (LineDao.findByName(name)) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
        Line line = LineDao.save(new Line(color, name));
        return new LineResponse(line);
    }

    public List<LineResponse> showLines() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        Line line = LineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 노선이 존재하지 않습니다."));
        return new LineResponse(line);
    }

    public void updateById(Long id, String color, String name) {
        LineDao.update(id, color, name);
    }

    public void deleteById(Long id) {
        LineDao.delete(id);
    }
}
