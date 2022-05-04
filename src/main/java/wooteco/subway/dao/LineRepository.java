package wooteco.subway.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LineRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineRepository(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line save(Line line) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor());

        long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return namedParameterJdbcTemplate.query(sql, rowMapper());
    }

    public Line findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql ,parameters, rowMapper());
    }

    private RowMapper<Line> rowMapper() {
        return (resultSet, rowNum) -> {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            return new Line(id, name, color);
        };
    }
}
