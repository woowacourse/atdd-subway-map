package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;

@Repository
public class LineDao {

    private static Long seq = 0L;
    private static final List<Line> lines = new ArrayList<>();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Line save(Line line) {
        Line savedLine = createNewObject(line);
        lines.add(savedLine);
        return savedLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        if (field != null) {
            field.setAccessible(true);
            ReflectionUtils.setField(field, line, ++seq);
        }
        return line;
    }

    public List<Line> findAllLines() {
        return new ArrayList<>(lines);
    }

    public void deleteAllLines() {
        lines.clear();
    }

    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst();
    }

    public Optional<Line> findByName(String name) {
        return lines.stream()
                .filter(line -> line.getName().equals(name))
                .findFirst();
    }

    public void updateLine(Long id, Line newLine) {
        Optional<Line> foundLine = findById(id);
        if (foundLine.isEmpty()) {
            throw new IllegalArgumentException("업데이트 할 노선이 존재하지 않습니다.");
        }
        lines.remove(foundLine.get());
        lines.add(new Line(id, newLine.getName(), newLine.getColor()));
    }

    public void deleteById(Long id) {
        Optional<Line> foundLine = findById(id);
        if (foundLine.isEmpty()) {
            throw new IllegalArgumentException("삭제할 노선이 존재하지 않습니다.");
        }
        lines.remove(foundLine.get());
    }
}
