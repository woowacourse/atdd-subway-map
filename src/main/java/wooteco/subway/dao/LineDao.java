package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.SectionV2;
import wooteco.subway.domain.SectionsV2;
import wooteco.subway.domain.Station;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public LineDao(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(Line line) {
        SqlParameterSource sqlParameter = new MapSqlParameterSource()
                .addValue("name", line.getName())
                .addValue("color", line.getColor());

        return simpleJdbcInsert.executeAndReturnKey(sqlParameter).longValue();
    }

    public Line findOnlyLineById(Long id) {
        String sql = "SELECT * FROM line WHERE id = :id";

        SqlParameterSource parameters = new MapSqlParameterSource("id", id);

        return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper());
    }

    public Line findById(Long id) {
        String sql = "SELECT l.id AS line_id, l.name, l.color,"
                + " s.id AS section_id,"
                + " s.up_station_id, us.name AS up_station_name,"
                + " s.down_station_id, ds.name AS down_station_name, s.distance"
                + " FROM line AS l"
                + " LEFT JOIN section AS s ON s.line_id = l.id"
                + " LEFT JOIN station AS us ON us.id = s.up_station_id"
                + " LEFT JOIN station AS ds ON ds.id = s.down_station_id"
                + " WHERE l.id = :id";

        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        LineSection lineSection = namedParameterJdbcTemplate.queryForObject(sql, parameters, lineAndSectionRowMapper());

        return new Line(
                lineSection.getLine().getId(),
                lineSection.getLine().getName(),
                lineSection.getLine().getColor(),
                new SectionsV2(List.of(lineSection.getSectionV2())));
    }

    public List<Line> findAll() {
        String sql = "SELECT l.id AS line_id, l.name, l.color,"
                + " s.id AS section_id,"
                + " s.up_station_id, us.name AS up_station_name,"
                + " s.down_station_id, ds.name AS down_station_name, s.distance"
                + " FROM line AS l"
                + " LEFT JOIN section AS s ON s.line_id = l.id"
                + " LEFT JOIN station AS us ON us.id = s.up_station_id"
                + " LEFT JOIN station AS ds ON ds.id = s.down_station_id";

        List<LineSection> lineSections = namedParameterJdbcTemplate.query(sql, lineAndSectionRowMapper());
        Map<Line, List<LineSection>> groupByLine = lineSections.stream()
                .collect(Collectors.groupingBy(LineSection::getLine));
        return groupByLine.keySet()
                .stream()
                .map(key -> new Line(key.getId(), key.getName(), key.getColor(), toSections(groupByLine.get(key))))
                .collect(Collectors.toList());
    }

    private SectionsV2 toSections(List<LineSection> lineSections) {
        return new SectionsV2(lineSections.stream()
                .map(LineSection::getSectionV2)
                .collect(Collectors.toList()));
    }

    public Long updateByLine(final Line line) {
        String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource nameParameters = new BeanPropertySqlParameterSource(line);

        namedParameterJdbcTemplate.update(sql, nameParameters);

        return line.getId();
    }

    public int deleteById(final Long id) {
        String sql = "DELETE FROM line WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);

        return namedParameterJdbcTemplate.update(sql, parameters);
    }

    private RowMapper<Line> rowMapper() {
        return (resultSet, rowNum) -> {
            Long lineId = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            return new Line(lineId, name, color);
        };
    }

    private RowMapper<LineSection> lineAndSectionRowMapper() {
        return (resultSet, rowNum) -> {
            Long lineId = resultSet.getLong("line_id");
            String name = resultSet.getString("name");
            String color = resultSet.getString("color");
            Line line = new Line(lineId, name, color);
            Long sectionId = resultSet.getLong("section_id");
            Long upStationId = resultSet.getLong("up_station_id");
            Long downStationId = resultSet.getLong("down_station_id");
            String upStationName = resultSet.getString("up_station_name");
            String downStationName = resultSet.getString("down_station_name");
            int distance = resultSet.getInt("distance");
            SectionV2 section = new SectionV2(sectionId,
                    lineId,
                    new Station(upStationId, upStationName),
                    new Station(downStationId, downStationName),
                    distance);
            return new LineSection(line, section);
        };
    }

    static class LineSection {

        private Line line;
        private SectionV2 sectionV2;

        public LineSection(Line line, SectionV2 sectionV2) {
            this.line = line;
            this.sectionV2 = sectionV2;
        }

        public Line getLine() {
            return line;
        }

        public SectionV2 getSectionV2() {
            return sectionV2;
        }
    }
}
