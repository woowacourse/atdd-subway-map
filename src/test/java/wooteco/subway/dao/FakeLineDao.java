package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;

public class FakeLineDao implements LineDao {

    private static Long seq = 0L;
    private static List<LineDto> lines = new ArrayList<>();

    @Override
    public LineDto save(LineDto lineDto) {
        boolean existName = lines.stream()
                .anyMatch(it -> it.getName().equals(lineDto.getName()));
        if (existName) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
        LineDto persistLine = createNewObject(lineDto);
        lines.add(persistLine);
        return persistLine;
    }

    private LineDto createNewObject(LineDto lineDto) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, lineDto, ++seq);
        return lineDto;
    }

    @Override
    public List<LineDto> findAll() {
        return lines;
    }

    @Override
    public LineDto findById(Long id) {
        return lines.stream()
                    .filter(line -> line.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 id입니다."));
    }

    @Override
    public LineDto update(LineDto updateLine) {
        lines.remove(findById(updateLine.getId()));

        LineDto line = new LineDto(updateLine.getId(), updateLine.getName(), updateLine.getColor());
        lines.add(line);

        return line;
    }

    @Override
    public void deleteById(Long id) {
        lines.remove(findById(id));
    }

    private boolean isSameName(Line line1, Line line2) {
        return line1.getName().equals(line2.getName());
    }
}
