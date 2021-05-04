package wooteco.subway.line;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {
    private Long seq = 0L;
    private List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistStation = createNewObject(line);
        lines.add(persistStation);
        return persistStation;
    }

    public Line findById(Long id) {
        return lines.stream()
                    .filter(line -> line.getId()
                                        .equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    public List<Line> findAll() {
        return lines;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
