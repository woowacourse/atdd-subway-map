package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JdbcSectionDao implements SectionDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
            resultSet.getInt("distance")
    );

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Section section) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<Section> findById(Long id) {
        final String sql = "select * from SECTION where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Section> findByLineId(Long lineId) {
        final String sql = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper, lineId);
    }

    @Override
    public Optional<Section> findBySameUpOrDownStationId(Long lineId, Section section) {
        final String sql = "select * from SECTION where (line_id = ? and up_station_id = ?) or" +
                " (line_id = ? and down_station_id = ?)";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper,
                    lineId, section.getUpStationId(), lineId, section.getDownStationId()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Section> findByUpStationId(Long lineId, Long upStationId) {
        final String sql = "select * from SECTION where line_id = ? and up_station_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, upStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Section> findByDownStationId(Long lineId, Long downStationId) {
        final String sql = "select * from SECTION where line_id = ? and down_station_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, sectionRowMapper, lineId, downStationId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateDownStation(Long id, Long downStationId, int newDistance) {
        final String sql = "update SECTION set down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, downStationId, newDistance, id);
    }

    @Override
    public void updateUpStation(Long id, Long upStationId, int newDistance) {
        final String sql = "update SECTION set up_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, upStationId, newDistance, id);
    }

    @Override
    public void delete(List<Section> sections) {
        final String sql = "delete from SECTION where id = ?";

        List<Long> sectionsId = sections.stream()
                .map(Section::getId)
                .collect(Collectors.toUnmodifiableList());
        for (Long sectionId : sectionsId) {
            jdbcTemplate.update(sql, sectionId);
        }
    }
}
