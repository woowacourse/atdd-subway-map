package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.entity.LineEntity;

@Repository
public class JdbcLineDao implements LineDao {

    private static final RowMapper<LineEntity> LINE_ROW_MAPPER = (resultSet, rowNum) -> new LineEntity(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("color")
    );

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcLineDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("line")
            .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(LineEntity entity) {
        final SqlParameterSource param = new BeanPropertySqlParameterSource(entity);
        return jdbcInsert.executeAndReturnKey(param).longValue();
    }

    @Override
    public List<LineEntity> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    @Override
    public Optional<LineEntity> findById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, LINE_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Long update(LineEntity entity) {
        final String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        final int updatedCount = jdbcTemplate.update(sql, entity.getName(), entity.getColor(), entity.getId());
        if (!isUpdated(updatedCount)) {
            return null;
        }
        return entity.getId();
    }

    private boolean isUpdated(int updatedCount) {
        return updatedCount != 0;
    }

    @Override
    public Long delete(Long id) {
        final String sql = "DELETE FROM line WHERE id = ?";
        final int deletedCount = jdbcTemplate.update(sql, id);
        if (!isUpdated(deletedCount)) {
            return null;
        }
        return id;
    }

}
