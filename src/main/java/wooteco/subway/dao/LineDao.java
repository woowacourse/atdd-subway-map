package wooteco.subway.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {

    private static final RowMapper<Line> lineRowMapper = (resultSet, rowNum) ->
        new Line(resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Line> findAll() {
        final String sql = "SELECT line.id    AS line_id,\n"
            + "       line.name  AS line_name,\n"
            + "       line.color AS line_color,\n"
            + "       section.id       AS section_id,\n"
            + "       section.distance AS section_distance,\n"
            + "       us.id     AS up_station_id,\n"
            + "       us.name   AS up_station_name,\n"
            + "       ds.id     AS down_station_id,\n"
            + "       ds.name   AS down_station_name\n"
            + "FROM line line\n"
            + "         LEFT OUTER JOIN section section ON line.id = section.line_id\n"
            + "         LEFT OUTER JOIN station us ON section.up_station_id = us.id\n"
            + "         LEFT OUTER JOIN station ds ON section.down_station_id = ds.id";

        Map<Long, List<Map<String, Object>>> resultLineById = jdbcTemplate.queryForList(sql,
                new EmptySqlParameterSource()).stream()
            .collect(Collectors.groupingBy(item -> (Long) item.get("line_id")));

        return resultLineById.values().stream()
            .map(this::toLine)
            .collect(Collectors.toList());
    }

    private Line toLine(List<Map<String, Object>> result) {
        if (result.isEmpty()) {
            throw new IllegalArgumentException("해당 노선 id로 저장된 구간이 존재하지 않습니다.");
        }

        List<Section> sections = findSections(result);

        return new Line(
            (Long) result.get(0).get("line_id"),
            (String) result.get(0).get("line_name"),
            (String) result.get(0).get("line_color"),
            new Sections(sections));
    }

    private List<Section> findSections(List<Map<String, Object>> result) {
        if (result.isEmpty() || result.get(0).get("section_id") == null) {
            return Collections.emptyList();
        }
        return result.stream()
            .collect(Collectors.groupingBy(it -> it.get("section_id")))
            .entrySet()
            .stream()
            .map(this::covertSection)
            .collect(Collectors.toList());
    }

    private Section covertSection(Map.Entry<Object, List<Map<String, Object>>> listEntry) {
        Map<String, Object> map = listEntry.getValue().get(0);

        Long id = (Long) listEntry.getKey();
        Long lineId = (Long) map.get("line_id");
        Station upStation = new Station(
            (Long) map.get("up_station_id"),
            (String) map.get("up_station_name"));
        Station downStation = new Station(
            (Long) map.get("down_station_id"),
            (String) map.get("down_station_name"));
        int distance = (int) map.get("section_distance");

        return new Section(id, lineId, upStation, downStation, distance);
    }

    public Line findById(Long id) {
        final String sql = "SELECT line.ID          AS line_id,\n"
            + "       line.name        AS line_name,\n"
            + "       line.color       AS line_color,\n"
            + "       section.ID       AS section_id,\n"
            + "       section.DISTANCE AS section_distance,\n"
            + "       us.ID           AS up_station_id,\n"
            + "       us.NAME         AS up_station_name,\n"
            + "       ds.ID           AS down_station_id,\n"
            + "       ds.NAME         AS down_station_name\n"
            + "FROM line line\n"
            + "         LEFT OUTER JOIN section section ON line.ID = section.LINE_ID\n"
            + "         LEFT OUTER JOIN station us ON section.UP_STATION_ID = us.ID\n"
            + "         LEFT OUTER JOIN station ds ON section.DOWN_STATION_ID = ds.ID\n"
            + "WHERE line.ID = :line_id";

        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("line_id", id);

        return toLine(jdbcTemplate.queryForList(sql, paramSource));
    }

    public Line save(Line line) {
        final String sql = "INSERT INTO line(name, color) VALUES(:name, :color)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);

        jdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"ID"});
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor());
    }

    public void update(Line line) {
        final String sql = "UPDATE line SET name = :name, color = :color "
            + "WHERE id = :id";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(line);

        jdbcTemplate.update(sql, paramSource);
    }

    public void deleteById(Long id) {
        final String sql = "DELETE FROM line WHERE id = :id";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        jdbcTemplate.update(sql, paramSource);
    }

    public boolean existByName(String name) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM line WHERE name = :name)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("name", name);

        return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) != 0;
    }

    public boolean existById(Long id) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM line WHERE id = :id)";
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("id", id);

        return jdbcTemplate.queryForObject(sql, paramSource, Integer.class) != 0;
    }
}
