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
        String sql = "SELECT s.id, s.up_station_id, s.down_station_id, s.distance FROM SECTION s " + "WHERE s" +
                ".line_id" + " = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final long id = rs.getLong("id");
            final long upStationId = rs.getLong("up_station_id");
            final long downStationId = rs.getLong("down_station_id");
            final int distance = rs.getInt("distance");
            return new Section(id, lineId, upStationId, downStationId, distance);
        }, lineId);
    }

    @Override
    public Optional<Section> findByLineIdAndUpStationId(final Section section) {
        String sql = "SELECT s.id, s.line_id, s.down_station_id, s.distance FROM SECTION s " + "WHERE s.up_station_id"
                + " = ? AND s.line_id = ?";
        try {
            final Long lineId = section.getLineId();
            final Long upStationId = section.getUpStationId();
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long id = rs.getLong("id");
                final long downStationId = rs.getLong("down_station_id");
                final int distance = rs.getInt("distance");
                return Optional.of(new Section(id, lineId, upStationId, downStationId, distance));
            }, upStationId, lineId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Section> findByLineIdAndDownStationId(final Section section) {
        String sql = "SELECT s.id, s.line_id, s.up_station_id, s.distance FROM SECTION s WHERE s.down_station_id" +
                " = ? AND s.line_id = ?";
        try {
            final Long lineId = section.getLineId();
            final Long downStationId = section.getDownStationId();
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long id = rs.getLong("id");
                final long upStationId = rs.getLong("up_station_id");
                final int distance = rs.getInt("distance");
                return Optional.of(new Section(id, lineId, upStationId, downStationId, distance));
            }, downStationId, lineId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Section> findByLineIdAndId(Long lineId, Long sectionId) {
        String sql = "SELECT s.id, s.line_id, s.up_station_id, s.down_station_id, s.distance FROM SECTION s " +
                "WHERE s.id = ? AND s.line_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                final long upStationId = rs.getLong("up_station_id");
                final long downStationId = rs.getLong("down_station_id");
                final int distance = rs.getInt("distance");
                return Optional.of(new Section(sectionId, lineId, upStationId, downStationId, distance));
            }, sectionId, lineId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateDownStationAndDistance(final Section section) {
        String sql = "UPDATE SECTION s SET s.down_station_id = ?, s.distance = ? WHERE s.down_station_id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDistance(), section.getDownStationId());
    }

    @Override
    public void updateUpStationAndDistance(final Section section) {
        String sql = "UPDATE SECTION s SET s.up_station_id = ?, s.distance = ? WHERE s.up_station_id = ?";
        jdbcTemplate.update(sql, section.getDownStationId(), section.getDistance(), section.getUpStationId());
    }

    @Override
    public void updateByLineIdAndDownStationId(final Section section) {
        String sql = "UPDATE SECTION s SET s.up_station_id = ?, s.distance = ? WHERE s.line_id = ? AND s" +
                ".down_station_id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDistance(), section.getLineId(),
                section.getDownStationId());
    }

    @Override
    public void deleteByLineIdAndUpStationId(final Section section) {
        String sql = "DELETE FROM SECTION s WHERE s.line_id = ? AND s.up_station_id = ?";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId());
    }

    @Override
    public void deleteByLineIdAndDownStationId(final Section section) {
        String sql = "DELETE FROM SECTION s WHERE s.line_id = ? AND s.down_station_id = ?";
        jdbcTemplate.update(sql, section.getLineId(), section.getDownStationId());
    }
}
