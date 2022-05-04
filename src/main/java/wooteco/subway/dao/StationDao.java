package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        validateStationName(station);

        String sql = "INSERT INTO station (name) VALUES (:name)";

        Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);

        long stationId = keyHolder.getKey().longValue();

        return new Station(stationId, station.getName());
    }

    private void validateStationName(Station station) {
        String sql = "SELECT id, name FROM station WHERE name = :name";

        Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());

        List<Station> stations = jdbcTemplate.query(sql, params,
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));

        if (stations.size() > 0) {
            throw new IllegalArgumentException("같은 이름의 역은 등록할 수 없습니다.");
        }
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, new MapSqlParameterSource(), (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id=:id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        int affected = jdbcTemplate.update(sql, params);

        if (affected == 0) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
    }
}
