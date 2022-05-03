package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    private static final Map<Long, Line> lines = new HashMap<>();

    public static Long save(Line line) {
        Line newLine = new Line(++seq, line.getName(), line.getColor());
        lines.put(seq, newLine);
        return seq;
    }

    public static List<Line> findAll() {
         return new ArrayList<>(lines.values());
    }

    public static void deleteById(Long lineId) {
        lines.remove(lineId);
    }

    public static Line findById(Long id) {
        return lines.get(id);
    }
}
