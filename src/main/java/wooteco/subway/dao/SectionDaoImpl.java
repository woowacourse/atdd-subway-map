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
public class SectionDaoImpl implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Section> sectionRowMapper() {
        return (rs, rowNum) ->
                new Section(rs.getLong("id"), rs.getLong("line_id"), rs.getLong("up_station_id"),
                        rs.getLong("down_station_id"), rs.getInt("distance"));
    }

    @Override
    public Section save(final Section section) {
        final String sql = "insert into Section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(), section.getLineId(), section.getUpStationId(),
                section.getDownStationId(), section.getDistance());
    }

    @Override
    public List<Section> findByLineId(long lineId) {
        final String sql = "select * from section where line_id = (?)";
        return jdbcTemplate.query(sql, sectionRowMapper(), lineId);
    }
}
