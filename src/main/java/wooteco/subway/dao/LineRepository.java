package wooteco.subway.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import javax.sql.DataSource;

@Repository
public class LineRepository {

    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineRepository(DataSource dataSource) {
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
}
