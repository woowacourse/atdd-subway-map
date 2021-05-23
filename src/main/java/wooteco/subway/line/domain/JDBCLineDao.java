package wooteco.subway.line.domain;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JDBCLineDao implements LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Line> lineRowMapper;

    public JDBCLineDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineRowMapper = (rs, rowNum) ->
                new Line(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("color"));
    }

    @Override
    public Line save(final Line line) {
        String sql = "INSERT INTO LINE(name, color) VALUES(?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.name());
            ps.setString(2, line.color());
            return ps;
        }, keyHolder);
        long newId = keyHolder.getKey().longValue();
        return new Line(newId, line.name(), line.color());
    }

    @Override
    public List<Line> findAll() {
        String sql = "select L.id as line_id, L.name as line_name, L.color as line_color, " +
                "S.id as section_id, S.distance as section_distance, " +
                "UST.id as up_station_id, UST.name as up_station_name, " +
                "DST.id as down_station_id, DST.name as down_station_name " +
                "from LINE L \n" +
                "left outer join SECTION S on L.id = S.line_id " +
                "left outer join STATION UST on S.up_station_id = UST.id " +
                "left outer join STATION DST on S.down_station_id = DST.id ";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        Map<Object, List<Map<String, Object>>> resultByLine =
                result.stream().collect(Collectors.groupingBy(it -> it.get("LINE_ID")));
        return resultByLine.values().stream()
                .map(this::mapToLine)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Line> findById(final Long id) {
        String sql = "select L.id as line_id, L.name as line_name, L.color as line_color, " +
                "S.id as section_id, S.distance as section_distance, " +
                "UST.id as up_station_id, UST.name as up_station_name, " +
                "DST.id as down_station_id, DST.name as down_station_name " +
                "from LINE L \n" +
                "left outer join SECTION S on L.id = S.line_id " +
                "left outer join STATION UST on S.up_station_id = UST.id " +
                "left outer join STATION DST on S.down_station_id = DST.id " +
                "WHERE L.id = ?";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id);
        return Optional.ofNullable(mapToLine(result));
    }

    private Line mapToLine(final List<Map<String, Object>> result) {
        if (result.size() == 0) {
            return null;
        }

        List<Section> sections = extractSections(result);

        return new Line(
                (Long) result.get(0).get("line_id"),
                (String) result.get(0).get("line_name"),
                (String) result.get(0).get("line_color"),
                sections
        );
    }

    private List<Section> extractSections(final List<Map<String, Object>> result) {
        if (result.isEmpty() || Objects.isNull(result.get(0).get("SECTION_ID"))) {
            return Collections.emptyList();
        }
        return result.stream()
                .collect(Collectors.groupingBy(it -> it.get("SECTION_ID")))
                .entrySet()
                .stream()
                .map(it ->
                        new Section(
                                (Long) it.getKey(),
                                new Line((Long) it.getValue().get(0).get("LINE_ID"), (String) it.getValue().get(0).get("LINE_NAME"), (String) it.getValue().get(0).get("LINE_COLOR")),
                                new Station((Long) it.getValue().get(0).get("UP_STATION_ID"), (String) it.getValue().get(0).get("UP_STATION_Name")),
                                new Station((Long) it.getValue().get(0).get("DOWN_STATION_ID"), (String) it.getValue().get(0).get("DOWN_STATION_Name")),
                                (int) it.getValue().get(0).get("SECTION_DISTANCE")))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Line> findByName(final String name) {
        String sql = "SELECT * FROM LINE WHERE name = ?";

        List<Line> lines = jdbcTemplate.query(sql, lineRowMapper, name);

        return Optional.ofNullable(DataAccessUtils.singleResult(lines));
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("DB 전체 삭제는 불가능!");
    }

    @Override
    public void update(final Line line) {
        String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ? ";

        jdbcTemplate.update(sql, line.name(), line.color(), line.id());
    }

    @Override
    public void delete(final Long id) {
        String sql = "DELETE FROM LINE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existByName(final String name) {
        String sql = "select count (name) from line where name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count > 0;
    }

    @Override
    public boolean existByColor(final String color) {
        String sql = "select count (color) from line where color = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, color);
        return count > 0;
    }
}
