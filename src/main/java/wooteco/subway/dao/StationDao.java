package wooteco.subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.SubwayException;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long insert(Station station) {
        String query = "INSERT INTO station(name) VALUES(?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        executeSaveQuery(station, query, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private void executeSaveQuery(Station station, String query, KeyHolder keyHolder) {
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
                ps.setString(1, station.getName());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new SubwayException(HttpStatus.CONFLICT, "이미 존재하는 역 이름은 추가할 수 없습니다.");
        }
    }

    public Station select(Long id) {
        String query = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.queryForObject(query,
                (resultSet, rowNum) -> {
                    Station station = new Station(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
                    );
                    return station;
                }, id);
    }

    public List<Station> selectAll() {
        String query = "SELECT * FROM station";
        List<Station> stations = jdbcTemplate.query(query, (resultSet, rowNum) -> {
            Station station = new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );
            return station;
        });
        return stations;
    }

    public void delete(Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        int affectedRowNumber = jdbcTemplate.update(query, id);

        if (affectedRowNumber == 0) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "역 이름이 존재하지 않아 삭제할 수 없습니다.");
        }
    }
}
