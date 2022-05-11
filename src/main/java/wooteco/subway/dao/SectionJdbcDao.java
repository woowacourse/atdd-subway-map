package wooteco.subway.dao;

import java.sql.PreparedStatement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Repository
public class SectionJdbcDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Long lineId, Section section) {
        final String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setLong(4, section.getDistance());
            return ps;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(), lineId, section.getUpStationId(),
                section.getDownStationId(), section.getDistance());
    }

    public Sections findById(Long lineId) {
        final String sql = "select * from section where line_id = (?)";
        return new Sections(jdbcTemplate.query(sql, (rs, rowNum) -> new Section(rs.getLong("id"),
                rs.getLong("line_id"), rs.getLong("up_station_id"),
                rs.getLong("down_station_id"), rs.getInt("distance")), lineId));
    }

    public void delete(Long lineId, Section section) {
        final String sql = "delete from section where line_id = ? and up_station_id = ? and down_station_id = ?";
        jdbcTemplate.update(sql, lineId, section.getUpStationId(), section.getDownStationId());
    }
}

