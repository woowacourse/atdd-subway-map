package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.repository.entity.LineEntity;

public class JdbcLineDao implements LineDao {

    @Override
    public LineEntity save(final LineEntity lineEntity) {
        return null;
    }

    @Override
    public List<LineEntity> findAll() {
        return null;
    }

    @Override
    public Optional<LineEntity> findByName(final String name) {
        return Optional.empty();
    }

    @Override
    public Optional<LineEntity> findById(final Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(final Long id) {

    }

    @Override
    public void update(final LineEntity newLineEntity) {

    }
}
