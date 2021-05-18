package wooteco.subway.section.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JdbcSectionDao implements SectionDao {
    private static final int LAST_SECTION = 1;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Section create(Section section, Long lineId) {
        String createSql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(createSql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Section.create(keyHolder.getKey().longValue(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    @Override
    public List<SectionTable> findAllByLineId(Long targetLineId) {
        String readSql = "SELECT * FROM section WHERE line_id = ?";

        List<SectionTable> sectionTables = jdbcTemplate.query(readSql, sectionRowMapper(), targetLineId);

        return sectionTables;
    }

    private RowMapper<SectionTable> sectionRowMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long lineId = rs.getLong("line_id");
            Long upStationId = rs.getLong("up_station_id");
            Long downStationId = rs.getLong("down_station_id");
            int distance = rs.getInt("distance");
            return new SectionTable(id, lineId, upStationId, downStationId, distance);
        };
    }

    @Override
    public void updateModified(Section section) {
        String updateSql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";

        jdbcTemplate.update(updateSql, section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance(), section.getId());
    }

    @Override
    public void remove(Long lineId, Long upStationId, Long downStationId) {
        String deleteSql = "DELETE FROM section WHERE line_id = ? AND up_station_id = ? AND down_station_id = ?";

        jdbcTemplate.update(deleteSql, lineId, upStationId, downStationId);
    }

    @Override
    public boolean isLast(Long lineId) {
        String countSql = "SELECT count(id) FROM section WHERE line_id = ?";

        int count = jdbcTemplate.queryForObject(countSql, int.class, lineId);

        return count == LAST_SECTION;
    }

}
