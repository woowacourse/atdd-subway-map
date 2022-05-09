package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        long savedId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return new Section(savedId, section.getLineId(),section.getUpStationId(),section.getDownStationId(),section.getDistance());
    }

    public List<Section> findByLineId(long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, rowMapper(), lineId);
    }

    private RowMapper<Section> rowMapper() {
        return (rs, rowNum) ->
            new Section(
                rs.getLong("id"),
                rs.getLong("line_id"),
                rs.getLong("up_station_id"),
                rs.getLong("down_station_id"),
                rs.getInt("distance")
            );
    }

    public void delete(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
