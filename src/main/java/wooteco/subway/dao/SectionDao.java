package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(final Long lineId, final Section section) {
        final String sql = "insert into SECTION (line_id, up_station_id, down_station_id, distance) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public boolean existStation(final Long lineId, final Long stationId) {
        final String sql = "select exists " +
                "(select * from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?))";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId);
    }

    public boolean existUpStation(final Long lineId, final Long stationId) {
        final String sql = "select exists " +
                "(select * from SECTION where line_id = ? and up_station_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId);
    }

    public boolean existDownStation(final Long lineId, final Long stationId) {
        final String sql = "select exists " +
                "(select * from SECTION where line_id = ? and down_station_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId);
    }

    public void delete(final Long id) {
        final String sql = "delete from SECTION where line_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
