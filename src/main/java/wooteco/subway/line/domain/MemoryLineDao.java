package wooteco.subway.line.domain;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryLineDao implements LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    public MemoryLineDao() {
    }

    @Override
    public Line save(final Line line) {
        if (findByName(line.name()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 역 입니다.");
        }
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    private Line createNewObject(final Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public Optional<Line> findById(final Long id) {
        return lines.stream()
                .filter(line -> line.sameId(id))
                .findAny();
    }

    @Override
    public Optional<Line> findByName(final String name) {
        return lines.stream()
                .filter(line -> line.sameName(name))
                .findAny();
    }

    @Override
    public void clear() {
        lines.clear();
        seq = 0L;
    }

    @Override
    public void update(final Long id, final String name, final String color) {
        Line line = findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
        line.changeName(name);
        line.changeColor(color);
    }

    @Override
    public void delete(Long id) {
        Line findLine = findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
        lines.remove(findLine);
    }
}
