package wooteco.subway.station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;

@Repository
public class StationDao {

    private static final int DESIRED_EXIST_STATION_COUNT_SIZE = 2;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNumber) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Station save(Station station) {
        String sql = "insert into STATION (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
                prepareStatement.setString(1, station.getName());
                return prepareStatement;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new SubwayCustomException(SubwayException.DUPLICATE_STATION_EXCEPTION);
        }
        return new Station(Objects.requireNonNull(keyHolder.getKey()).longValue(),
            station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public void delete(Long id) {
        try {
            String sql = "delete from STATION where id = ?";
            jdbcTemplate.update(sql, id);
        } catch (DataIntegrityViolationException e) {
            throw new SubwayCustomException(SubwayException.ILLEGAL_STATION_DELETE_EXCEPTION);
        }
    }

    public List<Station> findByLineId(Long lineId) {
        String sql = "select distinct id, name "
            + "from STATION join "
            + "(select distinct up_station_id, down_station_id from SECTION where line_id = ?) as t "
            + "on id = up_station_id or id = down_station_id";
        return jdbcTemplate.query(sql, stationRowMapper, lineId);
    }

    public boolean isExistStations(Long upStationId, Long downStationId) {
        String sql = "select count(*) as cnt from STATION where id = ? or id = ?";

        return Objects.requireNonNull(
            jdbcTemplate.queryForObject(sql, Integer.class, upStationId, downStationId))
            == DESIRED_EXIST_STATION_COUNT_SIZE;
    }
}
