package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.dto.LineResponse;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<LineResponse> queryById(Long id) {
        String sql =
            "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, s.id AS station_id, s.name AS station_name "
                + "FROM LINE AS l JOIN SECTION AS sec ON l.id = sec.line_id "
                + "JOIN STATION AS s ON s.id = sec.up_station_id or s.id = sec.down_station_id "
                + "WHERE l.id = ?";
        List<LineResponse> responses = jdbcTemplate.query(sql, new LineResponsesExtractor(), id);

        if (responses.size() == 1) {
            return Optional.of(responses.get(0));
        }
        return Optional.empty();
    }

    public List<LineResponse> queryAll() {
        String sql =
            "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, s.id AS station_id, s.name AS station_name "
                + "FROM LINE AS l JOIN SECTION AS sec ON l.id = sec.line_id "
                + "JOIN STATION AS s ON s.id = sec.up_station_id or s.id = sec.down_station_id";
        return jdbcTemplate.query(sql, new LineResponsesExtractor());
    }

}
