package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.LineDto;

@Component
public class LineDao implements CommonLineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public LineDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate, final DataSource dataSource) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Line save(final LineDto lineDto) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", lineDto.getName());
        params.put("color", lineDto.getColor());
        params.put("up_station_id", lineDto.getUpStationId());
        final Long id = simpleInsert.executeAndReturnKey(params).longValue();
        return new Line(id, lineDto.getName(), lineDto.getColor(), lineDto.getUpStationId());
    }

    @Override
    public List<Line> findAll() {
        final String sql = "select id, name, color, up_station_id, from LINE";
        return namedParameterJdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return new Line(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("color"), resultSet.getLong("up_station_id"));
        });
    }

    @Override
    public Line findById(final Long id) {
        final String sql = "select id, name, color, up_station_id from LINE where id = :id";
        final SqlParameterSource parameter = new MapSqlParameterSource(Map.of("id", id));
        return namedParameterJdbcTemplate.queryForObject(sql, parameter, (resultSet, rowNum) -> {
            return new Line(resultSet.getLong("id"), resultSet.getString("name"),
                    resultSet.getString("color"), resultSet.getLong("up_station_id"));
        });
    }

    @Override
    public int update(final Long id, final Line line) {
        final String sql = "update LINE set name = :name, color = :color where id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());
        params.put("id", id);
        final SqlParameterSource parameter = new MapSqlParameterSource(params);
        return namedParameterJdbcTemplate.update(sql, parameter);
    }

    @Override
    public int deleteById(final Long id) {
        final String sql = "delete from LINE where id = :id";
        return namedParameterJdbcTemplate.update(sql, Map.of("id", id));
    }

}
