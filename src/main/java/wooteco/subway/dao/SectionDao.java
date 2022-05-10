package wooteco.subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionDao {

    private static final RowMapper<Section> SECTION_MAPPER = (resultSet, rowNum) -> {
        Long id = resultSet.getLong("id");
        Station upStation = new Station(resultSet.getLong("us_id"), resultSet.getString("us_name"));
        Station downStation = new Station(resultSet.getLong("ds_id"), resultSet.getString("ds_name"));
        int distance = resultSet.getInt("distance");
        return new Section(id, upStation, downStation, distance);
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
    }

    public Section save(Line line, Section section) {
        SqlParameterSource parameters = new MapSqlParameterSource("line_id", line.getId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("distance", section.getDistance());
        Long id = simpleInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public List<Section> findAllByLineId(Line line) {
        String sql = "SELECT s.id AS id, s.line_id AS line_id, s.distance AS distance, us.id AS us_id,"
                + " us.name AS us_name, ds.id AS ds_id, ds.name AS ds_name FROM SECTION AS s"
                + " INNER JOIN STATION AS us ON s.up_station_id = us.id"
                + " INNER JOIN STATION AS ds ON s.down_station_id = ds.id"
                + " WHERE s.line_id = :line_id";
        SqlParameterSource parameters = new MapSqlParameterSource("line_id", line.getId());
        return jdbcTemplate.query(sql, parameters, SECTION_MAPPER);
    }

}
