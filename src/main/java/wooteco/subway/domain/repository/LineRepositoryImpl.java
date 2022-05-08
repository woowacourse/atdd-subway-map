package wooteco.subway.domain.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class LineRepositoryImpl implements LineRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private RowMapper<Line> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            return new Line(id, name, color, section);
        };
    }

    public LineRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Line save(final Line line) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor(), section);
    }

    @Override
    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return namedParameterJdbcTemplate.query(sql, rowMapper());
    }

    @Override
    public Optional<Line> findById(final Long id) {
        String sql = "SELECT * FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        List<Line> lines = namedParameterJdbcTemplate.query(sql, parameters, rowMapper());
        return getOptional(lines);
    }

    @Override
    public Optional<Line> findByName(final String name) {
        String sql = "SELECT * FROM line WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        List<Line> lines = namedParameterJdbcTemplate.query(sql, parameters, rowMapper());
        return getOptional(lines);
    }

    private Optional<Line> getOptional(List<Line> lines) {
        if (lines.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(lines.get(0));
    }

    @Override
    public void update(final Long id, final Line line) {
        String sql = "UPDATE line SET name = :name,  color = :color WHERE id = :id";

        SqlParameterSource nameParameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", line.getName())
                .addValue("color", line.getColor());

        namedParameterJdbcTemplate.update(sql, nameParameters);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public boolean existByName(String name) {
        final String sql = "SELECT COUNT(*) FROM line WHERE name = :name";
        SqlParameterSource parameters = new MapSqlParameterSource("name", name);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
        return count != 0;
    }
}
