package wooteco.subway.section.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class JdbcSectionDao implements SectionDao {
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionMapper = (rs, rowNum) -> new Section (
            rs.getLong("id"),
            new Line(rs.getLong("line_id"), null, null, null),
            new Station(rs.getLong("up_station_id"), null),
            new Station(rs.getLong("down_station_id"), null),
            rs.getInt("distance")
    );

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setLong(1, section.getLineId());
            pstmt.setLong(2, section.getUpStation().getId());
            pstmt.setLong(3, section.getDownStation().getId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, new Line(section.getLineId(), null, null), section);
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionMapper, lineId);
    }

    @Override
    public void delete(Section section) {
        String query = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(query, section.getId());
    }

    @Override
    public void deleteByLineId(Line line) {
        String query = "delete from SECTION where line_id = ?";
        jdbcTemplate.update(query, line.getId());
    }
}
