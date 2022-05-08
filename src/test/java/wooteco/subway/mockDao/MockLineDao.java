package wooteco.subway.mockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

public class MockLineDao implements LineDao {

    private static Long seq = 0L;
    private static final List<LineEntity> store = new ArrayList<>();

    public static void removeAll() {
        store.clear();
    }

    public LineEntity save(final LineEntity lineEntity) {
        if (existName(lineEntity.getName())) {
            throw new DataIntegrityViolationException("이름 중복되어서 라인 추가 불가능");
        }
        final LineEntity saved = new LineEntity(++seq, lineEntity.getName(), lineEntity.getColor());
        store.add(saved);
        return saved;
    }

    private boolean existName(final String name) {
        final Optional<LineEntity> any = store.stream()
                .filter(lineEntity -> lineEntity.getName().equals(name))
                .findAny();

        return any.isPresent();
    }

    public List<LineEntity> findAll() {
        return new ArrayList<>(store);
    }

    public Optional<LineEntity> findByName(final String name) {
        return store.stream()
                .filter(lineEntity -> lineEntity.getName().equals(name))
                .findAny();
    }

    public Optional<LineEntity> findById(final Long id) {
        return store.stream()
                .filter(lineEntity -> lineEntity.getId().equals(id))
                .findAny();
    }

    public void deleteById(final Long id) {
        findById(id).ifPresent(store::remove);
    }

    public void update(final LineEntity newLineEntity) {
        if (existNameForUpdate(newLineEntity.getId(), newLineEntity.getName())) {
            throw new DataIntegrityViolationException("이름 중복되어서 라인 추가 불가능");
        }
        findById(newLineEntity.getId()).ifPresent(oldLineEntity -> {
            store.remove(oldLineEntity);
            store.add(new LineEntity(oldLineEntity.getId(), newLineEntity.getName(), newLineEntity.getColor()));
        });
    }

    private boolean existNameForUpdate(final Long id, final String name) {
        final Optional<LineEntity> any = store.stream()
                .filter(lineEntity -> lineEntity.getName().equals(name) && !lineEntity.getId().equals(id))
                .findAny();
        return any.isPresent();
    }
}
