package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.datanotfound.DataNotFoundException;
import wooteco.subway.exception.datanotfound.LineNotFoundException;

public class FakeLineDao implements LineDao {

    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public static void init() {
        seq = 0L;
        lines = new ArrayList<>();
    }

    @Override
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

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Line findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId() == id)
                .findAny()
                .orElseThrow(() -> new LineNotFoundException("존재하지 않는 노선입니다."));
    }

    @Override
    public int update(Line line) {
        int targetIndex = IntStream.range(0, lines.size())
                .filter(index -> lines.get(index).getId() == line.getId())
                .findAny()
                .orElseThrow(() -> new LineNotFoundException("존재하지 않는 노선입니다."));
        lines.set(targetIndex, line);

        if (lines.get(targetIndex).getName().equals(line.getName())) {
            return 1;
        }
        return 0;
    }

    @Override
    public int delete(Long id) {
        int beforeSize = lines.size();
        lines = lines.stream()
                .filter(line -> line.getId() != id)
                .collect(Collectors.toList());
        if (lines.size() < beforeSize) {
            return 1;
        }
        return 0;
    }
}
