package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.notfound.NotFoundLineException;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public LineDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(final Line line) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        return insertActor.executeAndReturnKey(parameters).longValue();
    }

    public List<Line> findAll() {
        final String sql = "SELECT * FROM LINE";
        return jdbcTemplate.query(sql, rowMapper());
    }

    public Line find(final Long id) {
        try {
            final String sql = "SELECT * FROM LINE WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper(), id);
        } catch (final EmptyResultDataAccessException e) {
            throw new NotFoundLineException();
        }
    }

    public void update(final Long id, final String name, final String color) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, color, id);
    }

    public void delete(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(final Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM LINE WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    private RowMapper<Line> rowMapper() {
        return (resultSet, rowNum) -> new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
    }
}
