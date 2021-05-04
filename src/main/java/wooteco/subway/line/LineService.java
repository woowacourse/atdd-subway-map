package wooteco.subway.line;

public class LineService {
    public static LineResponse create(String color, String name) {
        if (LineDao.findByName(name)) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
        Line line = LineDao.save(new Line(color, name));
        return new LineResponse(line);
    }
}
