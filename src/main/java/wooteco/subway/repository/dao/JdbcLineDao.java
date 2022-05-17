package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.repository.entity.LineEntity;

@Repository
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInserter;

    public JdbcLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public LineEntity save(LineEntity line) {
        Map<String, String> params = Map.of("name", line.getName(), "color", line.getColor());
        long insertedId = simpleInserter.executeAndReturnKey(params).longValue();
        return new LineEntity(insertedId, line.getName(), line.getColor());
    }

    @Override
    public List<LineEntity> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    private RowMapper<LineEntity> getRowMapper() {
        return (resultSet, rowNumber) -> {
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            long id = resultSet.getLong("id");
            return new LineEntity(id, name, color);
        };
    }

    @Override
    public Optional<LineEntity> findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, getRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public int update(LineEntity line) {
        String sql = "UPDATE line set name = ?, color = ? where id = ?";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    @Override
    public Integer deleteById(Long id) {
        String sql = "DELETE FROM line where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
