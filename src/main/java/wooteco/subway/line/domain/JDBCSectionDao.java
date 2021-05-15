package wooteco.subway.line.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class JDBCSectionDao implements SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Section> sectionRowMapper;

    @Autowired
    public JDBCSectionDao(final JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate,
                (rs, rowNum) ->
                        new Section(rs.getLong("id"),
                                new Line(rs.getLong("line_id")),
                                new Station(rs.getLong("up_station_id")),
                                new Station(rs.getLong("down_station_id")),
                                rs.getInt("distance")));
    }

    public JDBCSectionDao(final JdbcTemplate jdbcTemplate, final RowMapper<Section> sectionRowMapper) {
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
                    ps.setLong(2, section.upStationId());
                    ps.setLong(3, section.downStationId());
                    ps.setInt(4, section.distance());
                    return ps;
                },
                keyHolder);

        long newId = keyHolder.getKey().longValue();
        return new Section(newId, section.line(), section.upStation(), section.downStation(), section.distance());
    }

    @Override
    public void delete(final Long id) {
        String sql = "DELETE FROM SECTION WHERE id = ?";
        int rowCount = jdbcTemplate.update(sql, id);

        if (rowCount == 0) {
            throw new IllegalStateException("존재하지 않는 id임");
        }
    }

    @Override
    public List<Section> findByLineId(final Long id) {
        String sql = "SELECT * FROM SECTION" +
                " LEFT OUTER JOIN LINE ON SECTION.line_id = LINE.id" +
                " WHERE Line.id = ?";

        return jdbcTemplate.query(sql, sectionRowMapper, id);
    }

    @Override
    public Optional<Section> findByLineIdWithUpStationId(final Long lineId, final Long stationId) {
        String sql = "SELECT * FROM SECTION" +
                " LEFT OUTER JOIN LINE ON SECTION.line_id = LINE.id" +
                " WHERE LINE.id = ? AND SECTION.up_station_id = ?";
        List<Section> sections = jdbcTemplate.query(sql, sectionRowMapper, lineId, stationId);

        return Optional.ofNullable(DataAccessUtils.singleResult(sections));
    }

    @Override
    public void deleteByLineIdWithUpStationId(Long lineId, Long upStationId) {
        String sql = "DELETE FROM SECTION" +
                " WHERE SECTION.line_id = ? AND SECTION.up_station_id = ?";
        jdbcTemplate.update(sql, lineId, upStationId);
    }

    @Override
    public Optional<Section> findByLineIdWithDownStationId(Long lineId, Long downStationId) {
        String sql = "SELECT * FROM SECTION" +
                " LEFT OUTER JOIN LINE ON SECTION.line_id = LINE.id" +
                " WHERE LINE.id = ? AND SECTION.down_station_id = ?";
        List<Section> sections = jdbcTemplate.query(sql, sectionRowMapper, lineId, downStationId);

        return Optional.ofNullable(DataAccessUtils.singleResult(sections));
    }

    @Override
    public void deleteByLineIdWithDownStationId(Long lineId, Long downStationId) {
        String sql = "DELETE FROM SECTION" +
                " WHERE SECTION.line_id = ? AND SECTION.down_station_id = ?";
        jdbcTemplate.update(sql, lineId, downStationId);
    }
}
