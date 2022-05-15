package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Repository
public class SectionDao {

    public static final RowMapper<Section> SECTION_ROW_MAPPER = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            new Distance(rs.getInt("distance"))
    );

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Sections findSectionsByLineId(Long lineId) {
        String sql = "SELECT id, up_station_id, down_station_id, distance FROM SECTION WHERE line_id = ?";

        List<Section> sections = jdbcTemplate.query(sql, SECTION_ROW_MAPPER, lineId);

        return new Sections(sections);
    }

    public Section save(Long lineId, Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance().getValue());
            return ps;
        }, keyHolder);

        Long createdId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(createdId, section);
    }

    public void removeAllSectionsByLineId(Long lineId) {
        String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
