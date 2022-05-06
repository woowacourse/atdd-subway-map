package wooteco.subway.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Repository
public class LineDao {

    public static final RowMapper<Line> ROW_MAPPER = (rs, rn) -> {
        Long newId = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");
        return new Line(newId, name, color);
    };

    private final JdbcTemplate jdbcTemplate;
    private final StationDao stationDao;

    public LineDao(JdbcTemplate jdbcTemplate, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationDao = stationDao;
    }

    public LineResponse queryByLineId(Long id) {
        List<StationResponse> stationResponses = stationDao.queryByLineId(id);

        return jdbcTemplate.queryForObject("SELECT id, name, color FROM LINE WHERE id = ?",
            (rs, rn) -> {
                long lineId = rs.getLong("id");
                String lineName = rs.getString("name");
                String lineColor = rs.getString("color");
                return new LineResponse(lineId, lineName, lineColor, stationResponses);
            }, id);
    }
}
