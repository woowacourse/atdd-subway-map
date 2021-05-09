package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.util.Optional;

@Repository
public class SectionJdbcDao implements SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Object> findById(final Long id) {
        String sql = "SELECT s.id, s.line_id, s.up_station_id, s.down_station_id, s.distance FROM SECTION s WHERE s" +
                ".id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long lineId = rs.getLong("line_id");
                final long upStationId = rs.getLong("up_station_id");
                final long downStationId = rs.getLong("down_station_id");
                final int distance = (int) rs.getLong("distance");
                return Optional.of(new Section(id, lineId, upStationId, downStationId, distance));
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
