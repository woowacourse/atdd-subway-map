package wooteco.subway.dao;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long create(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        String createSectionSql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder sectionKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(createSectionSql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, upStationId);
            ps.setLong(3, downStationId);
            ps.setInt(4, distance);
            return ps;
        }, sectionKeyHolder);
        return sectionKeyHolder.getKey().longValue();
    }

    public List<Section> findAll() {
        String query = "SELECT * FROM section";
        return jdbcTemplate.query(query, sectionRowMapper()
        );
    }

    public Optional<Section> findById(Long sectionId) {
        String query = "SELECT * FROM section WHERE id = ?";
        Section result = DataAccessUtils.singleResult(
                jdbcTemplate.query(query, sectionRowMapper(), sectionId));
        return Optional.ofNullable(result);
    }

    private RowMapper<Section> sectionRowMapper() {
        return (resultSet, rowNum) ->
                new Section(
                        resultSet.getLong("id"),
                        resultSet.getLong("line_id"),
                        resultSet.getLong("up_station_id"),
                        resultSet.getLong("down_station_id"),
                        resultSet.getInt("distance"));
    }

    public int edit(Long sectionId, Long lineId, Long upStationId, Long downStationId, int distance) {
        String query = "UPDATE section SET line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        return jdbcTemplate.update(query, lineId, upStationId, downStationId, distance, sectionId);
    }

    public int deleteById(Long sectionId) {
        String deleteSectionQuery = "DELETE FROM section WHERE id = ?";
        return jdbcTemplate.update(deleteSectionQuery, sectionId);
    }
}
