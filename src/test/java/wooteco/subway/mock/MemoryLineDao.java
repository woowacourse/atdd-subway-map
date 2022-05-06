package wooteco.subway.mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class MemoryLineDao implements LineDao {
    private static Long seq = 0L;
    private static Map<Long, Line> lines = new HashMap<>();

    @Override
    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        validateDuplicate(line);
        lines.put(persistLine.getId(), persistLine);
        return persistLine;
    }

    private void validateDuplicate(Line line) {
        boolean isDuplicate = lines.values().stream()
                .anyMatch(it -> it.isSameColor(line.getColor()) || it.isSameName(line.getName()));
        if (isDuplicate) {
            throw new DuplicateKeyException("이름이나 색깔이 중복된 노선은 만들 수 없습니다.");
        }
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public Line findById(Long id) {
        return lines.get(id);
    }

    @Override
    public List<Line> findAll() {
        return new ArrayList<>(lines.values());
    }

    @Override
    public int update(Line line) {
        validateDuplicate(line);
        lines.put(line.getId(), line);
        return 1;
    }

    @Override
    public int delete(Long id) {
        lines.remove(id);
        return 1;
    }

    public void clear() {
        lines.clear();
    }
}
