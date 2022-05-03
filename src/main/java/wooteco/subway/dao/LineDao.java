package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public Optional<Line> findByName(String name) {
        return lines.stream()
                .filter(line -> line.getName().equals(name))
                .findAny();
    }

    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny();
    }

    public List<Line> findAll() {
        return lines;
    }

    public void deleteAll() {
        lines = new ArrayList<>();
    }

    public void update(Long id, LineRequest lineRequest) {
        int size = lines.size();

        for (int i = 0; i < size; i++) {
            Line line = lines.get(i);
            updateByIndex(id, lineRequest, i, line.getId());
        }
    }

    private void updateByIndex(Long id, LineRequest lineRequest, int index, Long lineId) {
        if (id.equals(lineId)) {
            lines.set(index, new Line(id, lineRequest.getName(), lineRequest.getColor()));
        }
    }

    public void deleteById(Long id) {
        lines.removeIf(line -> id.equals(line.getId()));
    }
}
