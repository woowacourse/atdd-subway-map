package wooteco.subway.dao;

import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {

    private static final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
        new Station(resultSet.getLong("id"),
            resultSet.getString("name"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";

        List<Station> stations = jdbcTemplate.query(sql, new EmptySqlParameterSource(), stationRowMapper);
        return Collections.unmodifiableList(stations);
    }

    public Station save(Station station) {
        final String sql = "INSERT INTO station(name) VALUES (:name)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(station);
        try {
            jdbcTemplate.update(sql, paramSource, keyHolder);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("해당 이름의 지하철역을 생성할 수 없습니다.");
        }
        return new Station(keyHolder.getKey().longValue(), station.getName());
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = :id";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        int effectedRowCount = jdbcTemplate.update(sql, paramSource);
        if (effectedRowCount == 0) {
            throw new IllegalArgumentException("해당되는 역은 존재하지 않습니다.");
        }
    }
}
