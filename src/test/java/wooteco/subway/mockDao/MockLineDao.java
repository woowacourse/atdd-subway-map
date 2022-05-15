package wooteco.subway.mockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.dao.DataIntegrityViolationException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

public class MockLineDao implements LineDao {

    private static Long seq = 0L;
    private static final Map<Long, LineEntity> store = new ConcurrentHashMap<>();

    public static void removeAll() {
        store.clear();
    }

    public LineEntity save(final LineEntity lineEntity) {
        if (existName(lineEntity.getName())) {
            throw new DataIntegrityViolationException("이름 중복되어서 라인 추가 불가능");
        }
        final LineEntity saved = new LineEntity(++seq, lineEntity.getName(), lineEntity.getColor());
        store.put(saved.getId(), saved);
        return saved;
    }

    private boolean existName(final String name) {
        final Optional<LineEntity> any = store.values().stream()
                .filter(lineEntity -> lineEntity.getName().equals(name))
                .findAny();

        return any.isPresent();
    }

    public List<LineEntity> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<LineEntity> findByName(final String name) {
        return store.values().stream()
                .filter(lineEntity -> lineEntity.getName().equals(name))
                .findAny();
    }

    public Optional<LineEntity> findById(final Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void deleteById(final Long id) {
        store.remove(id);
    }

    public void update(final LineEntity newLineEntity) {
        if (existNameForUpdate(newLineEntity.getId(), newLineEntity.getName())) {
            throw new DataIntegrityViolationException("이름 중복되어서 라인 추가 불가능");
        }
        findById(newLineEntity.getId()).ifPresent(oldLineEntity -> {
            store.remove(oldLineEntity.getId());
            store.put(oldLineEntity.getId(),
                    new LineEntity(oldLineEntity.getId(), newLineEntity.getName(), newLineEntity.getColor()));
        });
    }

    private boolean existNameForUpdate(final Long id, final String name) {
        final Optional<LineEntity> any = store.values().stream()
                .filter(lineEntity -> lineEntity.getName().equals(name) && !lineEntity.getId().equals(id))
                .findAny();
        return any.isPresent();
    }
}
