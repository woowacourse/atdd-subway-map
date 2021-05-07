package wooteco.subway.line;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LineDao {

    private Long seq = 0L;
    private List<Line> lines  = new ArrayList<>();

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public Line save(Line line) {
        Line createdLine = createNewObject(line);
        lines.add(createdLine);
        return createdLine;
    }
}
