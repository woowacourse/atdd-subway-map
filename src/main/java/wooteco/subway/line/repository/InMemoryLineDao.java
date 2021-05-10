package wooteco.subway.line.repository;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.Line;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryLineDao implements LineRepository {
    private static AtomicLong seq = new AtomicLong();
    private static Map<Long, Line> lines = new ConcurrentHashMap<>();

    @Override
    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.put(persistLine.getId(), persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, seq.incrementAndGet());
        return line;
    }

    @Override
    public List<Line> findAll() {
        return lines.keySet().stream()
                .sorted()
                .map(id -> lines.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateDuplicateName(String name) {
        return lines.values().stream()
                .anyMatch(line -> line.isSameName(name));
    }

    @Override
    public Line findById(Long id) {
        return lines.values().stream()
                .filter(line -> line.isSameId(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public boolean validateUsableName(String oldName, String newName) {
        return lines.values().stream()
                .filter(line -> !line.isSameName(oldName))
                .anyMatch(line -> line.isSameName(newName));
    }

    @Override
    public void update(Line updatedLine) {
        Line line = findByIdIfExist(updatedLine.getId());
        lines.values().stream()
                .filter(line::equals)
                .findAny()
                .ifPresent(l -> l.update(updatedLine));
    }

    private Line findByIdIfExist(Long id) {
        return findById(id);
    }

    @Override
    public void delete(Long id) {
        Line line = findByIdIfExist(id);
        lines.remove(line);
    }
}
