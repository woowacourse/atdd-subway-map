package wooteco.subway.station.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicationException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.station.model.Station;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Station> mapperStation = (rs, rowNum) -> {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Station(id, name);
    };

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        validateDuplicatedName(station);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO station (name) VALUES (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private void validateDuplicatedName(Station station) {
        if (isDuplicate(station)) {
            throw new DuplicationException("이미 존재하는 역 이름입니다.");
        }
    }

    private boolean isDuplicate(Station newStation) {
        String sql = "SELECT id FROM station WHERE name = ?";
        try {
            jdbcTemplate.queryForObject(sql, Long.class, newStation.getName());
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public List<Station> findAll() {
        String sql = "SELECT id, name FROM station";
        return jdbcTemplate.query(sql, mapperStation);

    }

    public Station findStationById(long id) {
        String sql = "SELECT id, name FROM station WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapperStation, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 역 ID 입니다.");
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        int updateCount = jdbcTemplate.update(sql, id);
        if (updateCount == 0) {
            throw new NotFoundException("존재하지 않는 역 ID 입니다.");
        }
    }
}
