package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class LineJdbcDao {
    private JdbcTemplate jdbcTemplate;

    public LineJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
            new Line(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("color"));

    public Line save(String name, String color) {
        String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        }, keyHolder);
        Long createdLineId = keyHolder.getKey().longValue();
        return new Line(createdLineId, name, color);
    }

//    private static Station createNewObject(Station station) {
//        Field field = ReflectionUtils.findField(Station.class, "id");
//        field.setAccessible(true);
//        ReflectionUtils.setField(field, station, ++seq);
//        return station;
//    }
//
    public List<Line> findAll() {
        String query = "SELECT * FROM LINE";
        return jdbcTemplate.query(query, lineRowMapper);
    }
//
//    public static void deleteAll() {
//        lines.clear();
//    }
//
    public Optional<Line> findById(Long lineId) {
        String query = "SELECT * FROM LINE WHERE id = ?";
        Line result = DataAccessUtils.singleResult(
                jdbcTemplate.query(query, lineRowMapper, lineId)
        );
        return Optional.ofNullable(result);
    }
//
//    public static Long edit(Long lineId, String color, String name) {
//        Line foundLine = findById(lineId)
//                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
//        int index = lines.indexOf(foundLine);
//        lines.set(index, new Line(lineId, color, name));
//        return AFFECTED_ROWS_COUNT;
//    }
//
//    public static Long deleteById(Long lineId) {
//        Line foundLine = findById(lineId)
//                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
//        lines.remove(foundLine);
//        return AFFECTED_ROWS_COUNT;
//    }
}
