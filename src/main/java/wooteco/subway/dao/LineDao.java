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
        validateDuplicateName(line);
        Line newLine = new Line(++seq, line.getName(), line.getColor());
        lines.put(seq, newLine);
        return seq;
    }

    private static void validateDuplicateName(Line line) {
        if (lines.containsValue(line)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public static List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    public static boolean deleteById(Long lineId) {
        if (lines.containsKey(lineId)) {
            lines.remove(lineId);
            return true;
        }

        return false;
    }

    public static Line findById(Long id) {
        return lines.get(id);
    }

    public static boolean updateById(Long savedId, Line line) {
        if (lines.containsKey(savedId)) {
            lines.replace(savedId, line);
            return true;
        }

        return false;
    }
}
