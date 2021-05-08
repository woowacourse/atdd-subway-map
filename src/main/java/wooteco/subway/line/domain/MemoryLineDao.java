package wooteco.subway.line.domain;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryLineDao implements LineDao {
    private static Long seq = 0L;
    private static final List<LineEntity> lineEntities = new ArrayList<>();

    public MemoryLineDao() {
    }

    @Override
    public LineEntity save(final LineEntity lineEntity) {
        if (findByName(lineEntity.name()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 역 입니다.");
        }
        LineEntity persistLineEntity = createNewObject(lineEntity);
        lineEntities.add(persistLineEntity);
        return persistLineEntity;
    }

    @Override
    public List<LineEntity> findAll() {
        return lineEntities;
    }

    private LineEntity createNewObject(final LineEntity lineEntity) {
        Field field = ReflectionUtils.findField(LineEntity.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, lineEntity, ++seq);
        return lineEntity;
    }

    @Override
    public Optional<LineEntity> findById(final Long id) {
        return lineEntities.stream()
                .filter(line -> line.sameId(id))
                .findAny();
    }

    @Override
    public Optional<LineEntity> findByName(final String name) {
        return lineEntities.stream()
                .filter(line -> line.sameName(name))
                .findAny();
    }

    @Override
    public void clear() {
        lineEntities.clear();
        seq = 0L;
    }

    @Override
    public void update(final Long id, final String name, final String color) {
        LineEntity lineEntity = findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
        lineEntity.changeName(name);
        lineEntity.changeColor(color);
    }

    @Override
    public void delete(Long id) {
        LineEntity findLineEntity = findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
        lineEntities.remove(findLineEntity);
    }
}
