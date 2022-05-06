package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final StationDao stationDao;

    public LineDao(JdbcTemplate jdbcTemplate, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationDao = stationDao;
    }

    public Optional<LineResponse> queryById(Long id) {
        List<StationResponse> stationResponses = stationDao.queryByLineId(id);
        try {
            LineResponse response = jdbcTemplate
                .queryForObject("SELECT id, name, color FROM LINE WHERE id = ?",
                    (rs, rn) -> {
                        long lineId = rs.getLong("id");
                        String lineName = rs.getString("name");
                        String lineColor = rs.getString("color");
                        return new LineResponse(lineId, lineName, lineColor, stationResponses);
                    }, id);
            return Optional.of(response);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
