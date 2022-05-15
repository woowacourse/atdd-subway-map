package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.RegisteredSectionEntity;
import wooteco.subway.entity.StationEntity;

// TODO: 구현 후 SectionDao와의 통합 고려
@Repository
public class RegisteredSectionDao {

    private static final RowMapper<RegisteredSectionEntity> ROW_MAPPER = (resultSet, rowNum) -> {
        long lineId = resultSet.getLong("line_id");
        String lineName = resultSet.getString("line_name");
        String lineColor = resultSet.getString("line_color");
        long upStationId = resultSet.getLong("up_station_id");
        String upStationName = resultSet.getString("up_station_name");
        long downStationId = resultSet.getLong("down_station_id");
        String downStationName = resultSet.getString("down_station_name");
        int distance = resultSet.getInt("distance");

        LineEntity lineEntity = new LineEntity(lineId, lineName, lineColor);
        StationEntity upStationEntity = new StationEntity(upStationId, upStationName);
        StationEntity downStationEntity = new StationEntity(downStationId, downStationName);
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
}
