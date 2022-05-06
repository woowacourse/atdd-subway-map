package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

@Repository
public class StationDao {

    public static final RowMapper<Station> ROW_MAPPER = (rs, rowNum) -> {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Station(id, name);
    };
    private final JdbcTemplate jdbcTemplate;

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StationResponse> queryByLineId(Long lineId) {
        String sql = "SELECT distinct s.id as id, s.name as name FROM STATION AS s " +
            "JOIN SECTION AS sec ON s.id = sec.down_station_id OR s.id = sec.up_station_id " +
            "WHERE sec.line_id = ?";

        return jdbcTemplate.query(sql, (rs, rn) -> {
            long stationId = rs.getLong("id");
            String stationName = rs.getString("name");
            return new StationResponse(stationId, stationName);
        }, lineId);
    }
}
