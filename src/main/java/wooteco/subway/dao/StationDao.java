package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.Station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Station> STATION_ROW_MAPPER = ((rs, rowNum) ->
            new Station(rs.getLong("id"),
                    rs.getString("name"))
    );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        if (isContains(station)) {
            throw new IllegalStateException("중복된 지하철역을 저장할 수 없습니다.");
        }
        final String sql = "insert into Station (name) values (?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, station.getName());
            return statement;
        }, keyHolder);
        return new Station(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                station.getName()
        );
    }

    private boolean isContains(Station station) {
        final String sql = "select count(*) from Station where name = ?";
        final int count = jdbcTemplate.queryForObject(sql, Integer.class, station.getName());
        return count > 0;
    }

    public List<Station> findAll() {
        final String sql = "select id, name from Station";
        return jdbcTemplate.query(sql, STATION_ROW_MAPPER);
    }

    public void deleteById(Long id) {
        final String sql = "delete from Station where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
