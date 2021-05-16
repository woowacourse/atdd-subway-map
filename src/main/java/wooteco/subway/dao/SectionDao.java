package wooteco.subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Id;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Section> rowMapper;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource source) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(source)
            .withTableName("SECTION")
            .usingGeneratedKeyColumns("id");
        this.rowMapper = (rs, rowNum) -> {
            Long sectionId = rs.getLong("section_id");
            Long lineId = rs.getLong("line_id");
            String lineName = rs.getString("line_name");
            String lineColor = rs.getString("line_color");
            Long upStationId = rs.getLong("up_id");
            String upStationName = rs.getString("up_name");
            Long downStationId = rs.getLong("down_id");
            String downStationName = rs.getString("down_name");
            int distance = rs.getInt("distance");

            return new Section(
                new Id(sectionId),
                new Line(lineId, lineName, lineColor),
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName),
                new Distance(distance)
            );
        };
    }

    public Section save(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("line_id", section.getLineId());
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistanceValue());

        long key = jdbcInsert.executeAndReturnKey(params).longValue();

        return new Section(key, section);
    }

    public List<Section> findAllByLine(Line line) {
        String sql = "SELECT s.id AS section_id, line.id AS line_id, line.name AS line_name, line.color AS line_color, "
            + "up_table.id AS up_id, up_table.name AS up_name, "
            + "down_table.id AS down_id, down_table.name AS down_name, "
            + "distance "
            + "FROM section AS s "
            + "LEFT JOIN line ON s.line_id = line.id "
            + "LEFT JOIN station AS up_table ON s.up_station_id = up_table.id "
            + "LEFT JOIN station AS down_table ON s.down_station_id = down_table.id "
            + "WHERE s.line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, line.getId());
    }

    public Optional<Section> findByLineAndUpStation(Line line, Station upStation) {
        try {
            String sql = "SELECT s.id AS section_id, line.id AS line_id, line.name AS line_name, line.color AS line_color, "
                + "up_table.id AS up_id, up_table.name AS up_name, "
                + "down_table.id AS down_id, down_table.name AS down_name, "
                + "distance "
                + "FROM section AS s "
                + "LEFT JOIN line ON s.line_id = line.id "
                + "LEFT JOIN station AS up_table ON s.up_station_id = up_table.id "
                + "LEFT JOIN station AS down_table ON s.down_station_id = down_table.id "
                + "WHERE s.line_id = ? AND s.up_station_id = ?";
            return Optional
                .ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, line.getId(), upStation.getId()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Section> findByLineAndDownStation(Line line, Station downStation) {
        try {
            String sql = "SELECT s.id AS section_id, line.id AS line_id, line.name AS line_name, line.color AS line_color, "
                + "up_table.id AS up_id, up_table.name AS up_name, "
                + "down_table.id AS down_id, down_table.name AS down_name, "
                + "distance "
                + "FROM section AS s "
                + "LEFT JOIN line ON s.line_id = line.id "
                + "LEFT JOIN station AS up_table ON s.up_station_id = up_table.id "
                + "LEFT JOIN station AS down_table ON s.down_station_id = down_table.id "
                + "WHERE s.line_id = ? AND s.down_station_id = ?";
            return Optional
                .ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, line.getId(), downStation.getId()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int deleteByLineAndUpStation(Line line, Station upStation) {
        String sql = "DELETE FROM section WHERE line_id = ? AND up_station_id = ?";
        return jdbcTemplate.update(sql, line.getId(), upStation.getId());
    }

    public int deleteByLineAndDownStation(Line line, Station downStation) {
        String sql = "DELETE FROM section WHERE line_id = ? AND down_station_id = ?";
        return jdbcTemplate.update(sql, line.getId(), downStation.getId());
    }

    public int delete(Section section) {
        String sql = "DELETE FROM section WHERE id = ?";
        return jdbcTemplate.update(sql, section.getId());
    }
}
