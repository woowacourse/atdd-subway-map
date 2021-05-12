package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<SectionDbDto> sectionDtoRowMapper = (resultSet, rowNum) -> new SectionDbDto(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SectionDbDto save(long lineId, long upStationId, long downStationId, int distance) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, lineId);
            ps.setLong(2, upStationId);
            ps.setLong(3, downStationId);
            ps.setInt(4, distance);
            return ps;
        }, keyHolder);

        final long sectionId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new SectionDbDto(sectionId, lineId, upStationId, downStationId, distance);
    }

    public List<SectionDbDto> findByLineId(long lineId) {
        String sql = "SELECT * FROM SECTION WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionDtoRowMapper, lineId);
    }

    public void deleteSection(long lineId, Long upStationId, Long downStationId) {
        String sql = "DELETE FROM SECTION WHERE line_id = (?) AND up_station_id = (?) AND down_station_id = (?)";
        jdbcTemplate.update(sql, lineId, upStationId, downStationId);
    }

    public void deleteLine(long lineId) {
        String sql = "DELETE FROM SECTION WHERE line_id = (?)";
        jdbcTemplate.update(sql, lineId);
    }
}
