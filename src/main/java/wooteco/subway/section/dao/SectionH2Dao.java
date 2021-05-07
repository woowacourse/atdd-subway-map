package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;

import java.util.List;

@Repository
public class SectionH2Dao implements SectionDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) ->
            Section.of(resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance"));

    public SectionH2Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Section section) {
        String saveQuery = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(saveQuery,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        String findAllQuery = "SELECT * FROM section where line_id = ?";
        return jdbcTemplate.query(findAllQuery, sectionRowMapper, lineId);
    }

    @Override
    public void update(Section section) {
        String updateQuery = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(updateQuery,
            section.getUpStationId(),
            section.getDownStationId(),
            section.getDistance(),
            section.getId());
    }

    @Override
    public void delete(Long sectionId) {
        String deleteQuery = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(deleteQuery, sectionId);
    }
}
