package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

import java.util.*;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Station> resultMapper = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );

    public StationDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Optional<Station> findById(final Long id) {
        final String sql = "select * from STATION where id=:id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        final List<Station> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params), resultMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Optional<Station> findByName(final String name) {
        final String sql = "select * from STATION where name=:name";

        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        final List<Station> queryResult = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params), resultMapper);
        return Optional.ofNullable(DataAccessUtils.singleResult(queryResult));
    }

    public Station save(final Station station) {
        final String sql = "insert into STATION (name) values (:name)";

        final Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName());

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        return new Station(Objects.requireNonNull(keyHolder.getKey()).longValue(), station.getName());
    }

    public List<Station> findAll() {
        final String sql = "select * from STATION";

        return namedParameterJdbcTemplate.query(sql,
                (rs, rowNum) -> new Station(rs.getLong("id"), rs.getString("name")));
    }

    public int deleteById(Long id) {
        final String sql = "delete from STATION where id = :id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    public List<Station> findAllByLineId(final long lineId) {
        final String sql = "select distinct ST.id, ST.name from STATION ST, SECTION SE " +
                "where SE.line_id=:lineId and (ST.id=SE.up_station_id or ST.id=SE.down_station_id)";

        final Map<String, Object> params = new HashMap<>();
        params.put("lineId", lineId);

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(params), resultMapper);
    }
}
