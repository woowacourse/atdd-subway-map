package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LineJdbcDao {
    private JdbcTemplate jdbcTemplate;

    public LineJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line saveLineWithSection(String name, String color, Long upStationId, Long downStationId, int distance) {
        Long createdLineId = insertLine(name, color);
        Long createdSectionId = insertSection(createdLineId, upStationId, downStationId, distance);
        List<Section> sections = new ArrayList<>();
        Section section = new Section(createdSectionId, upStationId, downStationId, distance);
        sections.add(section);
        return new Line(createdLineId, name, color, sections);
    }

    private Long insertLine(String name, String color) {
        String insertLineSql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder lineKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertLineSql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        }, lineKeyHolder);
        return lineKeyHolder.getKey().longValue();
    }

    private Long insertSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        String insertSectionSql = "INSERT INTO SECTION " +
                "(line_id, up_station_id, down_station_id, distance) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder sectionKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSectionSql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, upStationId);
            ps.setLong(3, downStationId);
            ps.setInt(4, distance);
            return ps;
        }, sectionKeyHolder);
        return sectionKeyHolder.getKey().longValue();
    }

    public List<Line> findAllLine() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, (resultSet, rowNum) ->
                new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("color"))
        );
    }
//
//    public Optional<Line> findById(Long lineId) {
//        String query = "SELECT * FROM LINE WHERE id = ?";
//        Line result = DataAccessUtils.singleResult(
//                jdbcTemplate.query(query, lineRowMapper, lineId)
//        );
//        return Optional.ofNullable(result);
//    }
//
//    public Optional<Line> findByName(String name) {
//        String query = "SELECT * FROM LINE WHERE name = ?";
//        Line result = DataAccessUtils.singleResult(
//                jdbcTemplate.query(query, lineRowMapper, name)
//        );
//        return Optional.ofNullable(result);
//    }
//
//    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
//            new Line(
//                    resultSet.getLong("id"),
//                    resultSet.getString("name"),
//                    resultSet.getString("color"));
//
//    public Long edit(Long lineId, String color, String name) {
//        String query = "UPDATE LINE SET color = ?, name = ? WHERE id = ?";
//        return (long) jdbcTemplate.update(query, color, name, lineId);
//    }
//
    public Long deleteLineWithSectionByLineId(Long lineId) {
        String selectSectionQuery = "SELECT * FROM section WHERE line_id = ?";
        List<Long> sectionIds = jdbcTemplate.query(
                selectSectionQuery,
                (resultSet, rowNum) -> resultSet.getLong("id"),
                lineId
        );

        for (Long sectionId : sectionIds) {
            String deleteSectionQuery = "DELETE FROM section WHERE id = ?";
            jdbcTemplate.update(deleteSectionQuery, sectionId);
        }

        String deleteLineQuery = "DELETE FROM LINE WHERE id = ?";
        return (long) jdbcTemplate.update(deleteLineQuery, lineId);
    }
}
