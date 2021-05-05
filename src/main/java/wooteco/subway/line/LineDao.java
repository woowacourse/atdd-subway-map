package wooteco.subway.line;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public Line save(Line line) {
        Line persistLine  = createNewObject(line);
        add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    public List<Line> findAll() {
        return new ArrayList<>(lines);
    }

    public Optional<Line> findById(long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findFirst();
    }

    public boolean add(Line newLine) {
        return lines.add(newLine);
    }

    public boolean delete(Line line) {
        return lines.remove(line);
    }
}
