package wooteco.subway.station.dao;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.station.model.Station;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        validateDuplicatedName(station);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO station (name) VALUES (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                .prepareStatement(sql, new String[]{"id", "name"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        return new Station((Long) keys.get("id"), (String) keys.get("name"));
    }

    private void validateDuplicatedName(Station station) {
        if (isDuplicate(station)) {
            throw new DuplicationException("이미 존재하는 역 이름입니다.");
        }
    }

    private boolean isDuplicate(Station newStation) {
        String sql = "SELECT EXISTS(SELECT id FROM station WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, newStation.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT id, name FROM station";
        return jdbcTemplate.query(sql, mapperStation());

    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        int updateCount = jdbcTemplate.update(sql, id);
        if (updateCount == 0) {
            throw new NotFoundException("존재하지 않는 역 ID 입니다.");
        }
    }

    public Station findStationById(long id) {
        String sql = "SELECT id, name FROM station WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapperStation(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 역 ID 입니다.");
        }
    }

    public List<Station> findAllByIds(List<Long> stationIds) {

        String inSql = String.join(",", Collections.nCopies(stationIds.size(), "?"));

        String sql = "SELECT id, name FROM station WHERE id IN (%s)";
        return jdbcTemplate.query(String.format(sql, inSql), mapperStation(), stationIds.toArray());
    }

    private RowMapper<Station> mapperStation() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Station(id, name);
        };
    }
}
