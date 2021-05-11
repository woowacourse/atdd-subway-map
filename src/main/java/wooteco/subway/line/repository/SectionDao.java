package wooteco.subway.line.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Long lineId, Section section) {
        String sql = "INSERT INTO SECTION VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            final PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, lineId);
            pstmt.setLong(2, section.getUpStationId());
            pstmt.setLong(3, section.getDownStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        });

        return section;
    }

    public void saveAll(Long lineId, Sections sections) {
        List<Section> sectionList = sections.toList();
        jdbcTemplate.batchUpdate("INSERT INTO SECTION VALUES (?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                        ps.setLong(1, lineId);
                        ps.setLong(2, sectionList.get(i).getUpStationId());
                        ps.setLong(3, sectionList.get(i).getDownStationId());
                        ps.setInt(4, sectionList.get(i).getDistance());
                    }

                    @Override
                    public int getBatchSize() {
                        return sectionList.size();
                    }
                });
    }

    public List<Section> findAll(Long lineId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ?";

        return jdbcTemplate.query(sql, (rs, rn) -> {
            final Long upStationId = rs.getLong("up_station_id");
            final Long downStationId = rs.getLong("down_station_id");
            final int distance = rs.getInt("distance");
            return new Section(upStationId, downStationId, distance);
        }, lineId);
    }


    public Optional<Section> findSectionByUpStationId(final Long id, final Long upStationId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ? AND up_station_id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rn) -> Optional.ofNullable(
                new Section(rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"),
                        rs.getInt("distance"))));
    }
}
