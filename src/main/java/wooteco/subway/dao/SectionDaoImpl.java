package wooteco.subway.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
public class SectionDaoImpl implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Section> sectionRowMapper() {
        return (rs, rowNum) ->
                new Section(
                        rs.getLong("id"),
                        rs.getLong("line_id"),
                        new Station(rs.getLong("up_station_id"), rs.getString("up_name")),
                        new Station(rs.getLong("down_station_id"), rs.getString("dw_name")),
                        rs.getInt("distance"));
    }

    @Override
    public Section save(final Section section) {
        final String sql = "insert into Section (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStation().getId());
            ps.setLong(3, section.getDownStation().getId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);
        return new Section(keyHolder.getKey().longValue(), section.getLineId(), section.getUpStation(),
                section.getDownStation(), section.getDistance());
    }

    @Override
    public List<Section> findByLineId(long lineId) {
        final String sql = "select section.id as id, line_id, up_station_id, down_station_id, distance, "
                + "upstation.name as up_name, downstation.name as dw_name from section "
                + "join station as upstation on upstation.id = section.up_station_id "
                + "join station as downstation on downstation.id = section.down_station_id "
                + "where line_id = (?);";
        return jdbcTemplate.query(sql, sectionRowMapper(), lineId);
    }

    @Override
    public int update(List<Section> sections) {
        final String sql = "update section set (up_station_id, down_station_id, distance) = (?, ?, ?) where id = (?)";
        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, sections.get(i).getUpStation().getId());
                ps.setLong(2, sections.get(i).getDownStation().getId());
                ps.setInt(3, sections.get(i).getDistance());
                ps.setLong(4, sections.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return sections.size();
            }
        }).length;
    }

    @Override
    public int deleteByUpStationId(final Long stationId) {
        final String sql = "delete from section where up_station_id = (?)";
        return jdbcTemplate.update(sql, stationId);
    }
}
