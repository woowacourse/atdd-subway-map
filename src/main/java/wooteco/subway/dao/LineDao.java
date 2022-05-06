package wooteco.subway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
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
        String sql =
            "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, s.id AS station_id, s.name AS station_name "
                + "FROM LINE AS l JOIN SECTION AS sec ON l.id = sec.line_id "
                + "JOIN STATION AS s ON s.id = sec.up_station_id or s.id = sec.down_station_id "
                + "WHERE l.id = ?";
        List<LineResponse> responses = jdbcTemplate.query(sql, new LineResponseListExtractor(), id);

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
        return jdbcTemplate.query(sql, new LineResponseListExtractor());
    }

    private static class LineResponseListExtractor implements ResultSetExtractor<List<LineResponse>> {

        private final Set<Long> lineIds;
        private final Map<Long, List<StationResponse>> stations;
        private final Map<Long, String> names;
        private final Map<Long, String> colors;
        private final List<LineResponse> result;

        public LineResponseListExtractor() {
            lineIds = new HashSet<>();
            stations = new HashMap<>();
            names = new HashMap<>();
            colors = new HashMap<>();
            result = new ArrayList<>();
        }

        @Override
        public List<LineResponse> extractData(ResultSet rs) throws SQLException {
            while (rs.next()) {
                long lineId = rs.getLong("line_id");

                lineIds.add(lineId);
                names.put(lineId, rs.getString("line_name"));
                colors.put(lineId, rs.getString("line_color"));

                if (!stations.containsKey(lineId)) {
                    stations.put(lineId, new ArrayList<>());
                }

                List<StationResponse> responses = stations.get(lineId);
                responses.add(mapToStationResponse(rs));
            }

            for (long lineId : lineIds) {
                result.add(new LineResponse(lineId, names.get(lineId), colors.get(lineId),
                    stations.get(lineId)));
            }

            return result;
        }

        private StationResponse mapToStationResponse(ResultSet rs) throws SQLException {
            long id = rs.getLong("station_id");
            String name = rs.getString("station_name");
            return new StationResponse(id, name);
        }
    }
}
