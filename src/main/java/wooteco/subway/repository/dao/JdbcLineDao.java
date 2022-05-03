package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.LineEntity;

@Repository
public class JdbcLineDao implements LineDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcLineDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
