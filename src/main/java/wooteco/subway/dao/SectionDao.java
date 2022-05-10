package wooteco.subway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Repository
public class SectionDao {

    private static final RowMapper<Section> ROW_MAPPER = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance"));

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(final Section section) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update((Connection con) -> {
            PreparedStatement pstmt = con.prepareStatement(sql, new String[]{"id"});
            pstmt.setLong(1, section.getLineId());
            pstmt.setLong(2, section.getUpStationId());
            pstmt.setLong(3, section.getDownStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Sections findAllByLineId(final Long id) {
        final String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return new Sections(jdbcTemplate.query(sql, ROW_MAPPER, id));
    }

    public Section findById(final Long id) {
        final String sql = "SELECT * FROM SECTION WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, ROW_MAPPER, id);
    }

    public void deleteAllByLineId(final Long lineId) {
        final String sql = "DELETE FROM SECTION WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
