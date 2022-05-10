package wooteco.subway.dao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Station> eventRowMapper = (resultSet, rowNum)
            -> new Station(resultSet.getLong("id"), resultSet.getString("name"));

    public StationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(String name) {
        String insertSql = "insert into STATION (name) values (:name)";
        SqlParameterSource source = new MapSqlParameterSource("name", name);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(insertSql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Boolean existByName(String name) {
        String selectSql = "select EXISTS (select * from STATION where name = :name)";
        SqlParameterSource source = new MapSqlParameterSource("name", name);
        return Objects.requireNonNull(jdbcTemplate.queryForObject(selectSql, source, Boolean.class));
    }

    public Station findById(Long id) {
        String selectSql = "select * from STATION where id = :id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        Optional<Station> station = jdbcTemplate.query(selectSql, source, eventRowMapper).stream().findAny();
        return station.orElseThrow(() -> new IllegalStateException("지하철 역이 존재하지 않습니다."));
    }

    public List<Station> findAll() {
        String selectSql = "select * from STATION";
        return jdbcTemplate.query(selectSql, eventRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "delete from STATION where id = :id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, source);
    }
}
