package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.DuplicateNameException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LineFakeDao implements LineDao {
    static final List<Line> LINES = new ArrayList<>();
    private static Long seq = 0L;

    private static boolean isDuplicateLineName(Line line) {
        final String lineName = line.getName();
        return LINES.stream().anyMatch(storedLine -> storedLine.getName().equals(lineName));
    }

    @Override
    public Line save(final Line line) {
        if (isDuplicateLineName(line)) {
            throw new DuplicateNameException("이미 저장된 노선 이름입니다.");
        }

        Line persistLine = createNewObject(line);
        LINES.add(persistLine);
        return persistLine;
    }

    @Override
    public List<Line> findAll() {
        return LINES;
    }

    @Override
    public Optional<Line> findById(Long id) {
        return LINES.stream().filter(line -> line.getId().equals(id)).findAny();
    }

    @Override
    public Optional<Line> findByName(String name) {
        return LINES.stream().filter(line -> line.getName().equals(name)).findAny();
    }

    @Override
    public void update(Long id, Line line) {
        delete(id);
        LINES.add(line);
    }

    @Override
    public void delete(Long id) {
        LINES.removeIf(line -> line.getId().equals(id));
    }

    @Override
    public void updateTopStationId(final Section section) {
        final Line line = findById(section.getLineId()).orElseThrow(() -> new IllegalArgumentException("해당 ID와 일치하는 " +
                "역이 존재하지 않습니다."));
        final Line updatedLine = line.updateTopStationId(section.getUpStationId());
        update(section.getLineId(), updatedLine);
    }

    @Override
    public void updateBottomStationId(final Section section) {
        final Line line = findById(section.getLineId()).orElseThrow(() -> new IllegalArgumentException("해당 ID와 일치하는 " +
                "역이 존재하지 않습니다."));
        final Line updatedLine = line.updateBottomStationId(section.getDownStationId());
        update(section.getLineId(), updatedLine);
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }
}
