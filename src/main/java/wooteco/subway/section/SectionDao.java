package wooteco.subway.section;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.exception.StationExistenceException;
import wooteco.subway.station.exception.StationNotFoundException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final StationDao stationDao;

    public SectionDao(JdbcTemplate jdbcTemplate, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationDao = stationDao;
    }

    public Section save(Long lineId, Long upStationId, Long downStationId, int distance) {
        String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, String.valueOf(lineId));
            ps.setString(2, String.valueOf(upStationId));
            ps.setString(3, String.valueOf(downStationId));
            ps.setString(4, String.valueOf(distance));
            return ps;
        }, keyHolder);
        Station upStation = stationDao.findById(upStationId).orElseThrow(StationNotFoundException::new);
        Station downStation = stationDao.findById(downStationId).orElseThrow(StationNotFoundException::new);
        return new Section(keyHolder.getKey().longValue(), lineId, upStation, downStation, distance);
    }

    public int delete(Long id) {
        String sql = "delete from SECTION where ID = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int delete(Long lineId, Long stationId) {
        String sql = "delete from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        return jdbcTemplate.update(sql, lineId, stationId, stationId);
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionRowMapper(), lineId);
    }

    public List<Section> findAll() {
        String sql = "select id, line_id, up_station_id, down_station_id, distance from SECTION";
        return jdbcTemplate.query(sql, sectionRowMapper());
    }

    public void updateDistanceAndDownStation(Long lineId, Long upStationId, Long downStationId, int distance) {
        String query = "update SECTION set down_station_id = ?, distance = ? where line_id = ? and up_station_id = ?";
        jdbcTemplate.update(query, downStationId, distance, lineId, upStationId);
    }

    public int numberOfEnrolledSection(Long lineId) {
        String query = "select count(*) from SECTION where line_id = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, lineId);
    }

    private RowMapper<Section> sectionRowMapper() {
        return (rs, rowNum) ->
                new Section(
                        rs.getLong("id"),
                        rs.getLong("line_id"),
                        stationDao.findById(rs.getLong("up_station_id")).orElseThrow(StationExistenceException::new),
                        stationDao.findById(rs.getLong("down_station_id")).orElseThrow(StationExistenceException::new),
                        rs.getInt("distance")
                );
    }
}
