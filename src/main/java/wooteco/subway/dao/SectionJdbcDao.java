package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class SectionJdbcDao implements SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionJdbcDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(final Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return Section.ofSectionId(generatedId, section);
    }

    @Override
    public List<Section> findAllByLineId(final Long lineId) {
        String sql = "SELECT s.id, s.up_station_id, s.down_station_id, s.distance FROM SECTION s " +
                "WHERE s.line_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final long id = rs.getLong("id");
            final long upStationId = rs.getLong("up_station_id");
            final long downStationId = rs.getLong("down_station_id");
            final int distance = rs.getInt("distance");
            return new Section(id, lineId, upStationId, downStationId, distance);
        }, lineId);
    }

    @Override
    public Optional<Section> findSectionByUpStation(final Section section) {
        String sql = "SELECT s.id, s.line_id, s.down_station_id, s.distance FROM SECTION s " +
                "WHERE s.up_station_id = ? AND s.line_id = ?";
        try {
            final Long upStationId = section.getUpStationId();
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long id = rs.getLong("id");
                final long lineId = rs.getLong("line_id");
                final long downStationId = rs.getLong("down_station_id");
                final int distance = rs.getInt("distance");
                return Optional.of(new Section(id, lineId, upStationId, downStationId, distance));
            }, upStationId, section.getLineId());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Section> findById(final Long id) {
        String sql = "SELECT s.id, s.line_id, s.up_station_id, s.down_station_id, s.distance FROM SECTION s " +
                "WHERE s.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long lineId = rs.getLong("line_id");
                final long upStationId = rs.getLong("up_station_id");
                final long downStationId = rs.getLong("down_station_id");
                final int distance = rs.getInt("distance");
                return Optional.of(new Section(id, lineId, upStationId, downStationId, distance));
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateUpStationToDownStation(final Long upStationId, final Long downStationId) {
        String sql = "UPDATE SECTION s SET s.up_station_id = ? WHERE s.up_station_id = ?";
        jdbcTemplate.update(sql, downStationId, upStationId);
    }

    @Override
    public void updateDownStationToUpStation(final Long downStationId, final Long upStationId) {
        String sql = "UPDATE SECTION s SET s.down_station_id = ? WHERE s.down_station_id = ?";
        jdbcTemplate.update(sql, upStationId, downStationId);
    }
}
