package wooteco.subway.line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;

public class LineDao {
    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        Line persistLine = createNewObject(line);
        if(isDuplicatedName(persistLine)){
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
        lines.add(persistLine);
        return persistLine;
    }

    public static void delete(final Long id){
        final Line line = findById(id);
        lines.remove(line);
    }

    private static Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.isId(id))
                .findFirst()
                .orElseThrow(()-> new LineException("노선이 존재하지 않습니다."));
    }

    private static boolean isDuplicatedName(Line persistLine) {
        return lines.stream()
                .anyMatch(line -> line.sameName(persistLine));
    }

    public static List<Line> findAll() {
        return lines;
    }

    private static Line createNewObject(final Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
