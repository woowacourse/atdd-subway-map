package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.RegisteredSectionEntity;
import wooteco.subway.entity.StationEntity;

@Repository
public class RegisteredSectionDao {

    private static final RowMapper<RegisteredSectionEntity> ROW_MAPPER = (resultSet, rowNum) -> {
        LineEntity lineEntity = new LineEntity(
                resultSet.getLong("line_id"),
                resultSet.getString("line_name"),
                resultSet.getString("line_color"));
        StationEntity upStationEntity = new StationEntity(
                resultSet.getLong("up_station_id"), resultSet.getString("up_station_name"));
        StationEntity downStationEntity = new StationEntity(
                resultSet.getLong("down_station_id"), resultSet.getString("down_station_name"));
        int distance = resultSet.getInt("distance");
        return new RegisteredSectionEntity(lineEntity, upStationEntity, downStationEntity, distance);
    };

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RegisteredSectionDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RegisteredSectionEntity> findAll() {
        final String sql = "SELECT DISTINCT A.id AS line_id, A.name AS line_name, A.color AS line_color, "
                + "C.id AS up_station_id, C.name AS up_station_name, "
                + "D.id AS down_station_id, D.name AS down_station_name, B.distance "
                + "FROM line A, section B, station C, station D "
                + "WHERE A.id = B.line_id "
                + "AND (B.up_station_id = C.id AND B.down_station_id = D.id)";

        return jdbcTemplate.query(sql, new EmptySqlParameterSource(), ROW_MAPPER);
    }

    public List<RegisteredSectionEntity> findAllByLineId(Long lineId) {
        final String sql = "SELECT DISTINCT A.id AS line_id, A.name AS line_name, A.color AS line_color, "
                + "C.id AS up_station_id, C.name AS up_station_name, "
                + "D.id AS down_station_id, D.name AS down_station_name, B.distance "
                + "FROM line A, section B, station C, station D "
                + "WHERE (B.up_station_id = C.id AND B.down_station_id = D.id) "
                + "AND A.id = B.line_id AND A.id = :lineId";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("lineId", lineId);

        return jdbcTemplate.query(sql, paramSource, ROW_MAPPER);
    }
}
