package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Section section){
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public void update(Section section) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public void delete(Long lineId, Long stationId) {
        String sql = "delete from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }

    public List<Section> findByLineId(Long lineId) {
        String sql = String.format("select * from SECTION where line_id = %d", lineId);
        return jdbcTemplate.query(sql, new SectionMapper());
    }

    private static class SectionMapper implements RowMapper<Section> {
        public Section mapRow(ResultSet rs, int rowCnt) throws SQLException {
            return new Section(rs.getLong("id"), rs.getLong("line_id"),
                    rs.getLong("up_station_id"), rs.getLong("down_station_id"),
                    rs.getInt("distance"));
        }
    }
}
