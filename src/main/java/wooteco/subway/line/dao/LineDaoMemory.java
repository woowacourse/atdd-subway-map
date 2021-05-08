package wooteco.subway.line.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

public class LineDaoMemory implements LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        validateDuplicate(line);

        Line persistLine = createNewObject(line);
        lines.add(persistLine);

        return persistLine;
    }

    private void validateDuplicate(Line line) {
        if (lines.stream()
            .map(Line::getName)
            .anyMatch(name -> name.equals(line.getName()))) {
            throw new DuplicateLineException("[ERROR] 노선의 이름이 중복됩니다.");
        }

        if (lines.stream()
            .map(Line::getColor)
            .anyMatch(color -> color.equals(line.getColor()))) {
            throw new DuplicateLineException("[ERROR] 노선의 색이 중복됩니다.");
        }
    }

    @Override
    public Line show(Long id) {
        return lines.stream()
            .filter(element -> element.getId() == id)
            .findAny()
            .orElseThrow(() -> new NotFoundLineException("[Error] 해당 노선이 존재하지 않습니다."));
    }

    @Override
    public List<Line> showAll() {
        return lines;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);

        return line;
    }

    @Override
    public int countByName(String name) {
        List<Line> matchedlines = lines.stream()
            .filter(line -> (line.getName()).equals(name))
            .collect(Collectors.toList());
        return matchedlines.size();
    }

    @Override
    public int countByColor(String color) {
        List<Line> matchedLines = lines.stream()
            .filter(line -> (line.getColor()).equals(color))
            .collect(Collectors.toList());
        return matchedLines.size();
    }

    @Override
    public int update(long id, Line line) {
        Line targetLine = lines.stream()
            .filter(element -> element.getId() == id)
            .findAny()
            .orElseThrow(() -> new NotFoundLineException("[Error] 해당 노선이 존재하지 않습니다."));

        int index = lines.indexOf(targetLine);
        lines.remove(index);
        Line updatedLine = new Line(id, line.getName(), line.getColor());
        lines.add(updatedLine);
        return 1;
    }

    @Override
    public int delete(long id) {
        if (lines.removeIf(line -> line.getId() == id)) {
            return 1;
        }

        throw new NotFoundLineException("[Error] 해당 노선이 존재하지 않습니다.");
    }

    public void clean() {
        lines = new ArrayList<>();
    }
}
