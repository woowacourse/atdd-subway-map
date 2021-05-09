package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionDao {
    private static final RowMapper<SectionEntity> SECTION_ROW_MAPPER = (resultSet, rowNum) ->
            new SectionEntity(resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getInt("distance")
            );

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long lineId, Section section) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, section.upStation().getId(), section.downStation().getId(), section.distance().intValue());
    }

    public List<SectionEntity> findAllByLineId(Long lineId) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = (?)";
        return jdbcTemplate.query(sql, SECTION_ROW_MAPPER, lineId);
    }
}
