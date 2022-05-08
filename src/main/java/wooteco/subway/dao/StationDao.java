package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationSaveRequest;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StationDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Optional<Station> findById(Long id) {
        String sql = "select * from STATION where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        List<Station> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) ->
                        new Station(rs.getLong("id"), rs.getString("name")));
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Optional<Station> findByName(String name) {
        String sql = "select * from STATION where name = :name";

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        List<Station> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params),
                (rs, rowNum) ->
                        new Station(rs.getLong("id"), rs.getString("name")));
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Station save(StationSaveRequest stationSaveRequest) {
        String sql = "insert into STATION (name) values (:name)";

        Map<String, Object> params = new HashMap<>();
        params.put("name", stationSaveRequest.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        return new Station(Objects.requireNonNull(keyHolder.getKey()).longValue(), stationSaveRequest.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";

        return namedParameterJdbcTemplate.query(sql,
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public int deleteById(Long id) {
        String sql = "delete from STATION where id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }
}
