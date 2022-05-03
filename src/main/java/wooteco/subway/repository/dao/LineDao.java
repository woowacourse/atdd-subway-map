package wooteco.subway.repository.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.repository.entity.LineEntity;

public class LineDao {

    private static Long seq = 0L;
    private static List<LineEntity> store = new ArrayList<>();

    public static void removeAll() {
        store.clear();
    }

    public LineEntity save(final LineEntity lineEntity) {
        final LineEntity saved = new LineEntity(++seq, lineEntity.getName(), lineEntity.getColor());
        store.add(saved);
        return saved;
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
        findById(id).ifPresent(lineEntity -> store.remove(lineEntity));
    }
}
