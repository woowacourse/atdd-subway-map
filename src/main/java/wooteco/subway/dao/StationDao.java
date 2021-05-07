package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.BadRequestException;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
        new Station(
            resultSet.getLong("id"),
            resultSet.getString("name"));


    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        String sql = "INSERT INTO STATION (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            saveNewStation(station, sql, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new BadRequestException("해당 이름의 역이 이미 존재합니다.");
        }
        return keyHolder.getKey().longValue();
    }

    private void saveNewStation(Station station, String sql, KeyHolder keyHolder) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, station.getName());
            return ps;
        }, keyHolder);
    }

    public List<Station> findAll() {
        String query = "SELECT * FROM STATION";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    public long deleteById(Long id) {
        String query = "DELETE FROM STATION WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }
}
