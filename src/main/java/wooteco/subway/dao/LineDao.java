package wooteco.subway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(jdbcTemplate).withTableName("LINE").usingGeneratedKeyColumns("id");
    }

    public Line save(final Line line) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        Long generatedId = insertAction.executeAndReturnKey(params).longValue();
        return new Line(generatedId, line);
    }

    public List<Line> findAll() {
        String sql =
                "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, " +
                        "sec.id AS section_id, sec.distance AS distance, " +
                        "sec.up_station_id AS up_station_id, sec.down_station_id AS down_station_id, " +
                        "ups.name AS up_station_name, dos.name AS down_station_name " +
                        "FROM LINE l " +
                        "LEFT OUTER JOIN SECTION sec ON l.id = sec.line_id " +
                        "LEFT OUTER JOIN STATION ups ON sec.up_station_id = ups.id " +
                        "LEFT OUTER JOIN STATION dos ON sec.down_station_id = dos.id";

        return jdbcTemplate.query(sql, new LineExtractor());
    }

    public Optional<Line> findById(Long id) {
        String sql =
                "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, " +
                        "sec.id AS section_id, sec.distance AS distance, " +
                        "sec.up_station_id AS up_station_id, sec.down_station_id AS down_station_id, " +
                        "ups.name AS up_station_name, dos.name AS down_station_name " +
                        "FROM LINE l " +
                        "LEFT OUTER JOIN SECTION sec ON l.id = sec.line_id " +
                        "LEFT OUTER JOIN STATION ups ON sec.up_station_id = ups.id " +
                        "LEFT OUTER JOIN STATION dos ON sec.down_station_id = dos.id " +
                        "WHERE l.id = ?";

        final List<Line> lines = jdbcTemplate.query(sql, new LineExtractor(), id);
        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    public Optional<Line> findByName(String name) {
        String sql =
                "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, " +
                        "sec.id AS section_id, sec.distance AS distance, " +
                        "sec.up_station_id AS up_station_id, sec.down_station_id AS down_station_id, " +
                        "ups.name AS up_station_name, dos.name AS down_station_name " +
                        "FROM LINE l " +
                        "LEFT OUTER JOIN SECTION sec ON l.id = sec.line_id " +
                        "LEFT OUTER JOIN STATION ups ON sec.up_station_id = ups.id " +
                        "LEFT OUTER JOIN STATION dos ON sec.down_station_id = dos.id " +
                        "WHERE l.name = ?";

        final List<Line> lines = jdbcTemplate.query(sql, new LineExtractor(), name);
        final Line value = DataAccessUtils.singleResult(lines);
        return Optional.ofNullable(value);
    }

    public void updateTo(final Line line) {
        String sql = "UPDATE LINE l SET l.name = ?, l.color = ? WHERE l.id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM LINE l WHERE l.id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static final class LineExtractor implements ResultSetExtractor<List<Line>> {

        @Override
        public List<Line> extractData(ResultSet rs) throws SQLException, DataAccessException {
            final Map<Long, Line> lineMap = new HashMap<>();
            Line line;

            while (rs.next()) {
                Long lineId = rs.getLong("line_id");
                line = lineMap.get(lineId);
                if (line == null) {
                    String name = rs.getString("line_name");
                    String color = rs.getString("line_color");
                    Sections sections = new Sections(new ArrayList<>());
                    line = new Line(lineId, name, color, sections);
                    lineMap.put(lineId, line);
                }

                final Long upStationId = rs.getLong("up_station_id");
                final String upStationName = rs.getString("up_station_name");
                final Station upStation = new Station(upStationId, upStationName);

                final Long downStationId = rs.getLong("down_station_id");
                final String downStationName = rs.getString("down_station_name");
                final Station downStation = new Station(downStationId, downStationName);

                final Long sectionId = rs.getLong("section_id");
                final int distance = rs.getInt("distance");
                final Section section = new Section(sectionId, upStation, downStation, distance);
                lineMap.put(lineId, line.insertSection(section));
            }

            return new ArrayList<>(lineMap.values());
        }
    }
}
