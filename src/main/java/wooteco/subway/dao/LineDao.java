package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateLineName(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateLineName(Line line) {
        List<String> lineNames = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        String stationName = line.getName();

        if (lineNames.contains(stationName)) {
            throw new IllegalArgumentException("같은 이름의 노선은 등록할 수 없습니다.");
        }
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

    public static Line findById(Long id) {
        return lines.stream()
                .filter(it -> it.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }
}
