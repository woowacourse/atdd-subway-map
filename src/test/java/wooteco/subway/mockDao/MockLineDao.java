package wooteco.subway.mockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.entity.LineEntity;

public class MockLineDao implements LineDao {

    private static Long seq = 0L;
    private static final List<LineEntity> store = new ArrayList<>();

    public void removeAll() {
        store.clear();
    }

    public LineEntity save(final LineEntity lineEntity) {
        final long duplicateNameCount = store.stream()
                .filter(it -> it.getName().equals(lineEntity.getName()))
                .count();
        if (duplicateNameCount != 0) {
            throw new DuplicateKeyException(null);
        }
        final LineEntity lineEntityForSave = new LineEntity(++seq, lineEntity.getName(), lineEntity.getColor());
        store.add(lineEntityForSave);
        return lineEntityForSave;
    }

    public List<LineEntity> findAll() {
        return new ArrayList<>(store);
    }

    public Optional<LineEntity> findByName(final String name) {
        return store.stream()
                .filter(lineEntity -> lineEntity.getName().equals(name))
                .findAny();
    }

    public LineEntity findById(final Long id) {
        return store.stream()
                .filter(lineEntity -> lineEntity.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 노선을 찾을 수 없습니다."));
    }

    public void deleteById(final Long id) {
        store.remove(findById(id));
    }

    public void update(final LineEntity newLineEntity) {
        LineEntity oldLineEntity = findById(newLineEntity.getId());
        store.remove(oldLineEntity);
        store.add(new LineEntity(oldLineEntity.getId(), newLineEntity.getName(), newLineEntity.getColor()));
    }
}
