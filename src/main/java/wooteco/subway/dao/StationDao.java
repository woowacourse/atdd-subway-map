package wooteco.subway.dao;

import java.util.Collections;
import java.util.List;
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

    private static final String STATION_NOT_FOUND_EXCEPTION_MESSAGE = "해당되는 역은 존재하지 않습니다.";
    private static final String NAME_NOT_ALLOWED_EXCEPTION_MESSAGE = "해당 이름의 지하철역을 생성할 수 없습니다.";

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

        new StatementExecutor<>(() -> jdbcTemplate.update(sql, paramSource, keyHolder))
                .executeOrThrow(() -> new IllegalArgumentException(NAME_NOT_ALLOWED_EXCEPTION_MESSAGE));
        Number generatedId = keyHolder.getKey();
        return new Station(generatedId.longValue(), station.getName());
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM station WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        new StatementExecutor<>(() -> jdbcTemplate.update(sql, paramSource))
                .update()
                .throwOnNonEffected(() -> new IllegalArgumentException(STATION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(resultSet.getLong("id"),
                    resultSet.getString("name"));
}
