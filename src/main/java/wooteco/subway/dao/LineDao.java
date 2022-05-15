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

    public Optional<LineResponse> queryById(Long id, StationResponseSorter sorter) {
        String sql =
            "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, "
                + "up.id AS up_station_id, up.name AS up_station_name, "
                + "down.id AS down_station_id, down.name AS down_station_name "
                + "FROM LINE AS l JOIN SECTION AS sec ON l.id = sec.line_id "
                + "JOIN STATION AS up ON up.id = sec.up_station_id "
                + "JOIN STATION AS down ON down.id = sec.down_station_id "
                + "WHERE l.id = ?";
        List<LineResponse> responses = jdbcTemplate
            .query(sql, new LineResponsesExtractor(sorter), id);

        if (responses.size() == 1) {
            return Optional.of(responses.get(0));
        }
        return Optional.empty();
    }

    public List<LineResponse> queryAll(StationResponseSorter sorter) {
        String sql =
            "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, "
                + "up.id AS up_station_id, up.name AS up_station_name, "
                + "down.id AS down_station_id, down.name AS down_station_name "
                + "FROM LINE AS l JOIN SECTION AS sec ON l.id = sec.line_id "
                + "JOIN STATION AS up ON up.id = sec.up_station_id "
                + "JOIN STATION AS down ON down.id = sec.down_station_id ";
        return jdbcTemplate.query(sql, new LineResponsesExtractor(sorter));
    }

}
