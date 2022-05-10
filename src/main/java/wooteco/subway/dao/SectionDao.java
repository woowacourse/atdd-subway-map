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
import wooteco.subway.domain.SectionWithStation;
import wooteco.subway.domain.Station;

@Repository
public class SectionDao {
    private static final RowMapper<Section> SECTION_ROW_MAPPER = (rs, rowNum) -> new Section(
            rs.getLong("id"),
            rs.getLong("lineId"),
            rs.getLong("upStationId"),
            rs.getLong("downStationId"),
            rs.getInt("distance"));

    private static final RowMapper<SectionWithStation> SECTION_WITH_STATION_ROW_MAPPER = (rs, rowNum) -> new SectionWithStation(
            rs.getLong("id"),
            rs.getLong("lineId"),
            new Station(rs.getLong("upStationId"), rs.getString("upStationName")),
            new Station(rs.getLong("downStationId"), rs.getString("downStationName")),
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

    public List<SectionWithStation> findAllByLineId(Long lineId) {
        final String sql = "SELECT section.id, section.distance, "
                + "up.id AS upStationId, up.name AS upStationName, "
                + "down.id AS downStationId, down.name AS downStationName, "
                + "line.id AS lineId, line.name AS lineName, line.color AS lineColor FROM section "
                + "JOIN station AS up ON up.id = section.upStationId "
                + "JOIN station AS down ON down.id = section.downStationId "
                + "JOIN line ON line.id = section.lineId "
                + "WHERE section.lineId = ?;";
        return jdbcTemplate.query(sql, SECTION_WITH_STATION_ROW_MAPPER, lineId);
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

    public void delete(Long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAllByLineId(Long lineId) {
        final String sql = "DELETE FROM section WHERE lineId = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
