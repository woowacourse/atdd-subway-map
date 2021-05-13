package wooteco.subway.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.entity.SectionEntity;

@Repository
public class SectionDao {

    private static final RowMapper<SectionEntity> SECTION_ROW_MAPPER = (rs, rowNum) ->
        new SectionEntity(rs.getLong("id"), rs.getLong("line_id"),
            rs.getLong("up_station_id"), rs.getLong("down_station_id"),
            rs.getInt("distance"));

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SectionEntity save(Section section, Long lineId) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setLong(4, section.getDistance().value());
            return ps;
        }, keyHolder);

        return new SectionEntity(Objects.requireNonNull(keyHolder.getKey()).longValue(), lineId,
            section);
    }

    public List<SectionEntity> filterByLineId(Long lineId) {
        String sql = "SELECT * FROM section WHERE line_id = (?)";

        return jdbcTemplate.query(sql, SECTION_ROW_MAPPER, lineId);
    }

    public void remove(Long id) {
        String sql = "DELETE FROM section WHERE id = (?)";

        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM section WHERE id = (?)";

        return jdbcTemplate.queryForObject(sql, Integer.class, id) > 0;
    }
}
