package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.NameDuplicatedException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static Line save(Line line) {
        validateDuplicateName(line);
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private static void validateDuplicateName(Line line) {
        if(lines.contains(line)){
            throw new NameDuplicatedException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    public static List<Line> findAll() {
        return lines;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
