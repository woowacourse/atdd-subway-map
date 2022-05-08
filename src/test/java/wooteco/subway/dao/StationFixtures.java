package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class StationFixtures {

    public static void setUp(NamedParameterJdbcTemplate jdbcTemplate, String... names) {
        List<MapSqlParameterSource> params = new ArrayList<>();

        for (String name : names) {
            MapSqlParameterSource source = new MapSqlParameterSource();
            source.addValue("name", name);
            params.add(source);
        }

        jdbcTemplate.batchUpdate("INSERT INTO station (name) VALUES (:name) ", params.toArray(MapSqlParameterSource[]::new));
    }
}
