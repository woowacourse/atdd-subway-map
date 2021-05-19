package wooteco.subway.dao.section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

@Repository
public class JdbcSectionDao implements SectionDao{

    private static final RowMapper<Section> mapper = (rs, rowNum) -> {
        Long sectionId = rs.getLong("section_id");
        Long lineId = rs.getLong("line_id");
        Long upStationId = rs.getLong("up_id");
        String upStationName = rs.getString("up_name");
        Long downStationId = rs.getLong("down_id");
        String downStationName = rs.getString("down_name");
        int distance = rs.getInt("distance");
        return new Section(
            sectionId,
            lineId,
            new Station(upStationId, upStationName),
            new Station(downStationId, downStationName),
            distance
        );
    };

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((con) -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        return new Section(
            keyHolder.getKey().longValue(),
            section.getLineId(),
            section.getUpStation(),
            section.getDownStation(),
            section.getDistance()
        );
    }

    @Override
    public Optional<Section> findById(Long id) {
        String sql = "SELECT \n"
            + "  s.id AS section_id,\n"
            + "  line_id,\n"
            + "  up_table.id AS up_id,\n"
            + "  up_table.name AS up_name,\n"
            + "  down_table.id AS down_id,\n"
            + "  down_table.name AS down_name,\n"
            + "  distance\n"
            + "FROM\n"
            + "  section AS s\n"
            + "LEFT JOIN station AS up_table      ON s.up_station_id = up_table.id\n"
            + "LEFT JOIN station AS down_table ON s.down_station_id = down_table.id\n"
            + "WHERE s.id = ?;";
        return jdbcTemplate.query(sql, mapper, id)
            .stream().findAny();
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        String sql = "SELECT \n"
            + "  s.id AS section_id,\n"
            + "  line_id,\n"
            + "  up_table.id AS up_id,\n"
            + "  up_table.name AS up_name,\n"
            + "  down_table.id AS down_id,\n"
            + "  down_table.name AS down_name,\n"
            + "  distance\n"
            + "FROM\n"
            + "  section AS s\n"
            + "LEFT JOIN station AS up_table      ON s.up_station_id = up_table.id\n"
            + "LEFT JOIN station AS down_table ON s.down_station_id = down_table.id\n"
            + "WHERE s.line_id = ?;";
        return jdbcTemplate.query(sql, mapper, lineId);
    }

    @Override
    public void update(Section section) {
        String sql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(
            sql,
            section.getUpStation().getId(),
            section.getDownStation().getId(),
            section.getDistance(),
            section.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
