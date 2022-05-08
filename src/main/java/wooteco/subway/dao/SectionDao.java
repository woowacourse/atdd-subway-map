package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {
    private static final RowMapper<Section> SECTION_ROW_MAPPER = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("lineId"),
            rs.getLong("upStationId"),
            rs.getLong("downStationId"),
            rs.getInt("distance"));

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Long save(Section section) {
        final String sql = "INSERT INTO section (lineId, upStationId, downStationId, distance) VALUES (?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setLong(1, section.getLineId());
            preparedStatement.setLong(2, section.getUpStationId());
            preparedStatement.setLong(3, section.getDownStationId());
            preparedStatement.setInt(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Section> findAllByLineId(Long lineId) {
        final String sql = "SELECT * FROM section WHERE lineId = ?";
        return jdbcTemplate.query(sql, SECTION_ROW_MAPPER, lineId);
    }

    public Section findById(Long id) {
        final String sql = "SELECT * FROM section WHERE id = ?;";
        return jdbcTemplate.queryForObject(sql, SECTION_ROW_MAPPER, id);
    }

    public Section findByUpStationId(Long lineId, Long upStationId) {
        final String sql = "SELECT * FROM section WHERE upStationId = ? AND lineId = ?;";
        return jdbcTemplate.queryForObject(sql, SECTION_ROW_MAPPER, upStationId, lineId);
    }

    public Section findByDownStationId(Long lineId, Long downStationId) {
        final String sql = "SELECT * FROM section WHERE downStationId = ? AND lineId = ?;";
        return jdbcTemplate.queryForObject(sql, SECTION_ROW_MAPPER, downStationId, lineId);
    }

    public boolean hasUpStationId(Section section) {
        final String sql = "SELECT EXISTS (SELECT * FROM section WHERE upStationId = ? AND lineId = ?);";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sql, Boolean.class, section.getUpStationId(), section.getLineId()));
    }

    public boolean hasDownStationId(Section section) {
        final String sql = "SELECT EXISTS (SELECT * FROM section WHERE downStationId = ? AND lineId = ?);";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sql, Boolean.class, section.getDownStationId(), section.getLineId()));
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
