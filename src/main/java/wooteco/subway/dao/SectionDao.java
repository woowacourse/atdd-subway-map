package wooteco.subway.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {

    private static final String TABLE_NAME = "SECTION";
    private static final String KEY_NAME = "id";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(KEY_NAME);
    }

    public Section save(Section section) {
        SqlParameterSource sqlParameterSource = generateParameter(section);

        Long id = insertActor.executeAndReturnKey(sqlParameterSource).longValue();

        return findById(id).get();
    }

    public void saveAll(List<Section> sections) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) "
                + "values (:lineId, :upStationId, :downStationId, :distance)";

        SqlParameterSource[] batch = generateParameters(sections);
        jdbcTemplate.batchUpdate(sql, batch);
    }

    private SqlParameterSource[] generateParameters(List<Section> sections) {
        return sections.stream()
                .map(this::generateParameter)
                .toArray(MapSqlParameterSource[]::new);
    }

    private MapSqlParameterSource generateParameter(Section section) {
        return new MapSqlParameterSource("lineId", section.getLine().getId())
                .addValue("upStationId", section.getUpStation().getId())
                .addValue("downStationId", section.getDownStation().getId())
                .addValue("distance", section.getDistance());
    }

    public Optional<Section> findById(Long id) {
        String sql = "select s.id, "
                + "s.line_id, l.name as line_name, l.color as line_color, "
                + "s.up_station_id, up.name as up_station_name, "
                + "s.down_station_id, down.name as down_station_name, "
                + "s.distance "
                + "from SECTION s "
                + "join LINE l on s.line_id = l.id "
                + "join STATION up on s.up_station_id = up.id "
                + "join STATION down on s.down_station_id = down.id "
                + "where s.id = :id";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, Map.of("id", id), generateRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = "select s.id, "
                + "s.line_id, l.name as line_name, l.color as line_color, "
                + "s.up_station_id, up.name as up_station_name, "
                + "s.down_station_id, down.name as down_station_name, "
                + "s.distance "
                + "from SECTION s "
                + "join LINE l on s.line_id = l.id "
                + "join STATION up on s.up_station_id = up.id "
                + "join STATION down on s.down_station_id = down.id "
                + "where s.line_id = :lineId";

        return jdbcTemplate.query(sql, Map.of("lineId", lineId), generateRowMapper());
    }

    private RowMapper<Section> generateRowMapper() {
        return (resultSet, rowNum) -> {
            Long sectionId = resultSet.getLong("id");

            Long lineId = resultSet.getLong("line_id");
            String lineName = resultSet.getString("line_name");
            String lineColor = resultSet.getString("line_color");
            Line line = new Line(lineId, lineName, lineColor);

            Long upStationId = resultSet.getLong("up_station_id");
            String upStationName = resultSet.getString("up_station_name");
            Station upStation = new Station(upStationId, upStationName);

            Long downStationId = resultSet.getLong("down_station_id");
            String downStationName = resultSet.getString("down_station_name");
            Station downStation = new Station(downStationId, downStationName);

            int distance = resultSet.getInt("distance");

            return new Section(sectionId, line, upStation, downStation, distance);
        };
    }

    public void deleteByLineId(Long lineId) {
        String sql = "delete from SECTION where line_id = :lineId";
        jdbcTemplate.update(sql, Map.of("lineId", lineId));
    }
}
