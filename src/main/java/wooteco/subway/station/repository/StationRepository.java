package wooteco.subway.station.repository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class StationRepository {
    public static final int NO_EXIST_COUNT = 0;
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
            resultSet.getLong("id"),
            resultSet.getString("name")
    );

    public StationRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isExistName(final Station station) {
        String query = "SELECT EXISTS(SELECT * FROM STATION WHERE name = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, station.getName());
    }

    public boolean isExistId(final Long id) {
        String query = "SELECT EXISTS(SELECT * FROM STATION WHERE id = ?)";
        return jdbcTemplate.queryForObject(query, Boolean.class, id);
    }

    public Station save(final Station station) {
        try {
            String query = "INSERT INTO STATION(name) VALUES (?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(Connection -> {
                PreparedStatement ps = Connection.prepareStatement(query, new String[]{"id"});
                ps.setString(1, station.getName());
                return ps;
            }, keyHolder);
            return new Station(Objects.requireNonNull(keyHolder.getKey()).longValue(), station.getName());
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException("중복되는 StationName 입니다.");
        }
    }

    public List<Station> getStations() {
        String query = "SELECT id, name FROM station ORDER BY id";
        return jdbcTemplate.query(query, stationRowMapper);
    }

    public void deleteById(final Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        int affectedRowCount = jdbcTemplate.update(query, id);
        if (affectedRowCount == NO_EXIST_COUNT) {
            throw new NotFoundException("존재하지 않는 id 입니다.");
        }
    }
}
