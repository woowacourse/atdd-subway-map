package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.entity.RegisteredStationEntity;

@Repository
public class RegisteredStationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RegisteredStationDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<RegisteredStationEntity> FULL_ROW_MAPPER = (resultSet, rowNum) -> {
        StationEntity stationEntity = new StationEntity(
                resultSet.getLong("station_id"),
                resultSet.getString("station_name"));
        LineEntity lineEntity = new LineEntity(
                resultSet.getLong("line_id"),
                resultSet.getString("name"),
                resultSet.getString("color"));
        return new RegisteredStationEntity(stationEntity, lineEntity);
    };

    public List<RegisteredStationEntity> findAll() {
        final String sql = "SELECT DISTINCT A.id AS line_id, A.name, A.color, "
                + "C.id AS station_id, C.name AS station_name "
                + "FROM line A, section B, station C "
                + "WHERE A.id = B.line_id "
                + "AND (B.up_station_id = C.id OR B.down_station_id = C.id)";

        return jdbcTemplate.query(sql, new EmptySqlParameterSource(), FULL_ROW_MAPPER);
    }
}
