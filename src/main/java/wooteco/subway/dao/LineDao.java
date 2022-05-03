package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;

import wooteco.subway.domain.Line;

public class LineDao {

    private static final LineDao INSTANCE = new LineDao();
    private final List<Line> lines = new ArrayList<>();
    private long seq = 0L;

    private LineDao() {
    }

    public static LineDao getInstance() {
        return INSTANCE;
    }

    public Line save(Line line) {
        Line persistStation = createNewObject(line);
        lines.add(persistStation);
        return persistStation;
    }

    private Line createNewObject(Line line) {
        return new Line(++seq, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        return List.copyOf(lines);
    }
}
