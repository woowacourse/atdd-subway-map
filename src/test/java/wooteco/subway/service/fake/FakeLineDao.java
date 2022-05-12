package wooteco.subway.service.fake;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class FakeLineDao implements LineDao {

    private static final int DELETE_SUCCESS = 1;

    private static Long seq = 0L;
    private final List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        if (lines.contains(line)) {
            throw new DuplicateKeyException("동일한 line이 존재합니다.");
        }

        final Line newLine = createNewObject(line);
        lines.add(newLine);
        return newLine;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Line> findAll() {
        return List.copyOf(lines);
    }

    @Override
    public Long updateByLine(Line updateLine) {
        final Line findLine = lines.stream()
                .filter(line -> line.getId().equals(updateLine.getId()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
        lines.remove(findLine);
        lines.add(updateLine);

        return updateLine.getId();
    }

    @Override
    public int deleteById(Long id) {
        final Line findLine = lines.stream()
                .filter(line -> line.getId().equals(id))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);

        lines.remove(findLine);
        return DELETE_SUCCESS;
    }

    private static Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
