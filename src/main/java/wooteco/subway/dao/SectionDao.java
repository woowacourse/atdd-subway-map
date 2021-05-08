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
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) ->
        new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance"));


    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) "
            + "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section);
    }

    public List<Section> findAllByLineIdAndStationId(Long lineId, Long stationId) {
        String query = "SELECT * FROM SECTION "
            + "WHERE (line_id = ?) AND (up_station_id = ? OR down_station_id = ?)";
        return jdbcTemplate.query(query, sectionRowMapper, lineId, stationId, stationId);
    }

    public List<Section> findAllByLineId(Long lineId) {
        String query = "SELECT * FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionRowMapper, lineId);
    }

    public long deleteById(Long id) {
        String query = "DELETE FROM SECTION WHERE id = ?";
        return jdbcTemplate.update(query, id);
    }

    public long deleteAllByLineId(Long lineId) {
        String query = "DELETE FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.update(query, lineId);
    }
}
