package wooteco.subway.line.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class DBSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper;

    @Autowired
    public DBSectionDao(final JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate,
                (rs, rowNum) ->
                        new Section(rs.getLong("id"),
                                rs.getLong("line_id"),
                                rs.getLong("up_station_id"),
                                rs.getLong("up_station_id"),
                                rs.getInt("distance")));
    }

    public DBSectionDao(final JdbcTemplate jdbcTemplate, final RowMapper<Section> sectionRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionRowMapper = sectionRowMapper;
    }

    @Override
    public Section save(final Section section) {
        String sql = "INSERT INTO SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                    ps.setLong(1, section.lineId());
                    ps.setLong(2, section.upsStationId());
                    ps.setLong(3, section.downStationId());
                    ps.setInt(4, section.distance());
                    return ps;
                },
                keyHolder);

        long newId = keyHolder.getKey().longValue();
        return new Section(newId, section.lineId(), section.upsStationId(), section.downStationId(), section.distance());
    }

    @Override
    public List<Section> findAll() {
        return null;
    }

    @Override
    public Optional<Section> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {

    }
}
