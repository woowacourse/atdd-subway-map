package wooteco.subway.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Repository
public class JdbcStationDao implements StationDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Station station) {
        String query = "INSERT INTO Station (name) values (?)";
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(query, new String[]{"id"});
            preparedStatement.setString(1, station.getName());
            return preparedStatement;
        }, generatedKeyHolder);
        return Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT id, name from Station";
        return jdbcTemplate.query(query,
                (resultSet, rowNum) -> new Station(
                        resultSet.getLong(1),
                        resultSet.getString(2)
                ));
    }

    @Override
    public Station findById(Long id) {
        String query = "SELECT id, name from Station WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(query,
                    (resultSet, rowNum) -> new Station(
                            resultSet.getLong(1),
                            resultSet.getString(2)
                    ), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("해당 id에 맞는 지하철 역이 없습니다.");
        }
    }

    @Override
    public Boolean existsByName(String name) {
        String query = "SELECT COUNT(*) FROM Station WHERE name=?";
        int count = jdbcTemplate.queryForObject(query,
                (resultSet, rowNum) -> resultSet.getInt(1),
                name);
        return count != 0;
    }

    @Override
    public void remove(Long id) {
        String query = "DELETE FROM Station WHERE id=?";
        jdbcTemplate.update(query, id);
    }
}
