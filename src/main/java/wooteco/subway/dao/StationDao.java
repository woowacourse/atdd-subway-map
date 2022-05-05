package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {
    protected static final String DUPLICATE_EXCEPTION_MESSAGE = "이름이 중복된 역은 만들 수 없습니다.";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    public Station insert(Station station) {
        String name = station.getName();
        validateDuplicateName(name);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name);

        long id = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Station(id, name);
    }

    private void validateDuplicateName(String stationName) {
        List<Station> stations = findAll();
        boolean isDuplicate = stations.stream()
                .anyMatch(station -> station.isSameName(stationName));
        if (isDuplicate) {
            throw new IllegalArgumentException(DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
