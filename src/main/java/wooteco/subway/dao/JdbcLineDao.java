package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.LineDuplicateException;

@Repository
public class JdbcLineDao implements LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
        new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"));

    public JdbcLineDao(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("line")
            .usingGeneratedKeyColumns("id");
    }

    public Line save(final Line line) {
        checkDuplicateName(line);
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    private void checkDuplicateName(final Line line) {
        if (existsSameNameLine(line)) {
            throw new LineDuplicateException("이미 존재하는 노선입니다.");
        }
    }

    private boolean existsSameNameLine(final Line line) {
        final String sql = "SELECT EXISTS (SELECT * FROM line WHERE name = :name)";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, parameters, Boolean.class));
    }

    @Override
    public Optional<Line> findById(final Long id) {
        final String sql = "SELECT * FROM line WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, parameters, lineRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        final String sql = "TRUNCATE TABLE line";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
        final String resetIdSql = "ALTER TABLE line ALTER COLUMN id RESTART WITH 1";
        namedParameterJdbcTemplate.update(resetIdSql, new MapSqlParameterSource());
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return namedParameterJdbcTemplate.query(sql, lineRowMapper);
    }

    @Override
    public void update(final Line line) {
        final String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void delete(final Line line) {
        final String sql = "DELETE FROM line WHERE id = :id";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

}
