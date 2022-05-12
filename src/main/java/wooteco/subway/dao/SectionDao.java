package wooteco.subway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionEntity;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final Section section) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update((Connection con) -> {
            PreparedStatement pstm = con.prepareStatement(sql, new String[]{"id"});
            pstm.setLong(1, section.getLineId());
            pstm.setLong(2, section.getUpStation().getId());
            pstm.setLong(3, section.getDownStation().getId());
            pstm.setInt(4, section.getDistance());
            return pstm;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void updateAll(final List<Section> sections) {
        final String sql = "UPDATE SECTION SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                final Section section = sections.get(i);
                ps.setLong(1, section.getUpStation().getId());
                ps.setLong(2, section.getDownStation().getId());
                ps.setInt(3, section.getDistance());
                ps.setLong(4, section.getId());
            }

            @Override
            public int getBatchSize() {
                return sections.size();
            }
        });
    }

    public List<SectionEntity> findAllByLineId(final Long lineId) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new SectionEntity(
                rs.getLong("id"),
                rs.getLong("line_id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance")
        ), lineId);
    }
}
