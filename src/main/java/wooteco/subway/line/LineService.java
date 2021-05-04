package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;

public class LineService {
    public static LineResponse create(String color, String name) {
        if (LineDao.findByName(name)) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
        Line line = LineDao.save(new Line(color, name));
        return new LineResponse(line);
    }

    public static List<LineResponse> showLines() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public static LineResponse showLine(Long id) {
        Line line = LineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 노선이 존재하지 않습니다."));
        return new LineResponse(line);
    }
}
