package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        if (lines.contains(line)) {
            throw new IllegalArgumentException("이미 존재하는 노선입니다.");
        }
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public static List<Line> findAll() {
        return lines;
    }

    public static Line findById(final Long id) {
         return lines.stream()
                .filter(it -> it.getId() == id)
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
    }

    public static void deleteAll(){
        seq = 0L;
        lines = new ArrayList<>();
    }

    public static void deleteById(Long id) {
        lines.remove(findById(id));
    }

    public static void update(Long id, LineRequest lineRequest) {
        Line targetLine = LineDao.findById(id);
        targetLine.update(lineRequest);
    }
}
