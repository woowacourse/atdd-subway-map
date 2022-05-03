package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.ClientException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
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

    public static Line find(Long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findAny()
                .orElseThrow(() -> new ClientException("존재하지 않는 노선입니다."));
    }

    public static void update(Long id, Line line) {
        int targetIndex = IntStream.range(0, lines.size())
                .filter(index -> lines.get(index).getId() == id)
                .findAny()
                .orElseThrow(() -> new ClientException("존재하지 않는 노선입니다."));
        lines.set(targetIndex, line);
    }

    public static void delete(Long id) {
        lines = lines.stream()
                .filter(line -> line.getId() != id)
                .collect(Collectors.toList());
    }
}
