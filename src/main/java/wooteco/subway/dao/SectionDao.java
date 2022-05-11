package wooteco.subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Section section){
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public void update(Section section) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId());
    }

    public void delete(Long lineId, Long stationId) {
        String sql = "delete from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        jdbcTemplate.update(sql, lineId, stationId, stationId);
    }

    public List<Section> findSectionsIn(Line line) {
        String sql = String.format("select * from SECTION where line_id = %d", line.getId());
        return jdbcTemplate.query(sql, new SectionMapper());
    }

    public boolean existByLineAndStation(Long lineId, Long stationId){
        String sql = "select EXISTS (select id from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?))";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId));
    }

    public boolean existConnectedTo(Section section){
        String sql = "select EXISTS (select id from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ? or up_station_id = ? or down_station_id = ?))";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class,
                section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDownStationId(), section.getUpStationId()));
    }

    public Optional<Section> getSectionsOverLappedBy(Section section) {
        String sql = String.format("select * from SECTION where line_id = %d and (up_station_id = %d or down_station_id = %d)", section.getLineId(), section.getUpStationId(), section.getDownStationId());

        return createOptionalSectionBy(sql);
    }

    public Integer countSectionsIn(Long lineId) {
        String sql = "select count(id) from SECTION where line_id = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, lineId);
    }

    public Optional<Section> findSectionHavingDownStationOf(Long lineId, Long stationId) {
        String sql = String.format("select * from SECTION where line_id = %d and down_station_id = %d",
                lineId, stationId);

        return createOptionalSectionBy(sql);
    }

    public Optional<Section> findSectionHavingUpStationOf(Long lineId, Long stationId) {
        String sql = String.format("select * from SECTION where line_id = %d and up_station_id = %d",
                lineId, stationId);

        return createOptionalSectionBy(sql);
    }

    private static class SectionMapper implements RowMapper<Section> {
        public Section mapRow(ResultSet rs, int rowCnt) throws SQLException {
            return new Section(rs.getLong("id"), rs.getLong("line_id"),
                    rs.getLong("up_station_id"), rs.getLong("down_station_id"),
                    rs.getInt("distance"));
        }
    }

    private Optional<Section> createOptionalSectionBy(String sql) {
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new SectionMapper()));
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }
}
