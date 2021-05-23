package wooteco.subway.station.infra;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import wooteco.subway.exception.badrequest.DuplicatedNameException;
import wooteco.subway.exception.badrequest.NoRowAffectedException;
import wooteco.subway.exception.notfound.StationNotFoundException;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JdbcStationDao implements StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) ->
            new Station(
                    resultSet.getLong("id"),
                    resultSet.getString("name")
            );

    public JdbcStationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Station save(final Station station) {
        try {
            String query = "INSERT INTO station(name) VALUES(?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
                ps.setString(1, station.getName());
                return ps;
            }, keyHolder);
            return findById(keyHolder.getKey().longValue()).orElseThrow(StationNotFoundException::new);
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException("이미 존재하는 지하철 역 이름입니다.", e.getCause());
        }
    }

    @Override
    public Optional<Station> findById(final Long id) {
        String query = "SELECT * FROM station WHERE id = ?";
        return Optional.ofNullable(this.jdbcTemplate.queryForObject(query, stationRowMapper, id));
    }

    @Override
    public List<Station> findByIds(List<Long> ids) {
        String parseIds = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String query = String.format("SELECT * FROM station WHERE id IN (%s)", parseIds);
        return jdbcTemplate.query(query, stationRowMapper);
    }

    @Override
    public List<Station> findAll() {
        String query = "SELECT * FROM station";
        return this.jdbcTemplate.query(query, stationRowMapper);
    }

    @Override
    public void delete(final Long id) {
        String query = "DELETE FROM station WHERE id = ?";
        int update = this.jdbcTemplate.update(query, id);
        if (update < 1) {
            throw new NoRowAffectedException("지하철 역을 삭제하는데 실패했습니다.");
        }
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM station";
        this.jdbcTemplate.update(query);
    }
}
