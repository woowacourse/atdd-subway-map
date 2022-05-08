package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getLong("distance")
    );

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setLong(4, section.getDistance());
            return ps;
        }, keyHolder);
        long insertedId = keyHolder.getKey().longValue();

        return new Section(insertedId, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = (?)";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }
}
