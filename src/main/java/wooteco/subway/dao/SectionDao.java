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

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) ->
            new Section(resultSet.getLong("id"),
                    resultSet.getLong("line_id"),
                    resultSet.getLong("up_station_id"),
                    resultSet.getLong("down_station_id"),
                    resultSet.getLong("distance"));

    public Section save(Section section, Long lineId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, String.valueOf(lineId));
            ps.setString(2, String.valueOf(section.getUpStationId()));
            ps.setString(3, String.valueOf(section.getDownStationId()));
            ps.setString(4, String.valueOf(section.getDistance()));
            return ps;
        }, keyHolder);
        long insertedId = keyHolder.getKey().longValue();

        return new Section(insertedId, lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public List<Section> findAll(Long lineId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from section where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    public Section findById(Long id) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from section where id = (?)";
        return jdbcTemplate.queryForObject(sql, sectionRowMapper, id);
    }

    public void deleteById(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteByLineId(Long lineId) {
        String sql = "delete from section where line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public void deleteByLineIdAndStationIds(Long lineId, Long upStationId, Long downStationId) {
        String sql = "delete from section where line_id = ? and up_station_id = ? and down_station_id = ?";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId);
    }
}
