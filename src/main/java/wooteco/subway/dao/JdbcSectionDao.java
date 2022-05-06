package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(final Section section) {
        final String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values(?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section);
    }

    @Override
    public boolean existByUpStationIdAndDownStationId(final long upStationId, final long downStationId) {
        final String sql = "select exists (select * from SECTION "
                + "where (up_station_id = ? and down_station_id = ?) or (down_station_id = ? and up_station_id = ?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, upStationId, downStationId, upStationId, downStationId);
    }
}
