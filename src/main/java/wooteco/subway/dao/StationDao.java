package wooteco.subway.dao;

import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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

    public Long save(Station station) {
        String sql = "insert into STATION (name) values (:name)";
        SqlParameterSource source = new BeanPropertySqlParameterSource(station);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean existsByName(String name) {
        String sql = "select exists (select 1 from STATION where name = :name)";
        SqlParameterSource source = new MapSqlParameterSource("name", name);
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, source, Boolean.class));
    }

    public boolean existsById(Long id) {
        String sql = "select exists (select 1 from STATION where id = :id)";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        return Objects.requireNonNull(jdbcTemplate.queryForObject(sql, source, Boolean.class));
    }

    public Station findById(Long id) {
        String sql = "select * from STATION where id = :id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(sql, source, eventRowMapper);
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, eventRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "delete from STATION where id = :id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, source);
    }

    public List<Station> findByLineId(Long id) {
        String sql = "select st.* "
                + "from (select distinct up_station_id ,down_station_id from section where line_id = :id) sc, station st "
                + "where sc.up_station_id = st.id OR sc.down_station_id = st.id";
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, source, eventRowMapper);
    }
}
