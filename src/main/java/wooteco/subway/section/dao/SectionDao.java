package wooteco.subway.section.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Section> sectionRowMapper() {
        return (resultSet, rowNum) -> {
            Long id = resultSet.getLong("id");
            Station upStation = new Station(
                    resultSet.getLong("up_station_id"),
                    resultSet.getString("up_station_name")
            );
            Station downStation = new Station(
                    resultSet.getLong("down_station_id"),
                    resultSet.getString("down_station_name")
            );
            int distance = resultSet.getInt("distance");

            return new Section(upStation, downStation, distance);
        };
    }

    public void save(Long lineId, SectionRequest sr) {
        String sql = "insert into SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lineId, sr.getUpStationId(), sr.getDownStationId(), sr.getDistance());
    }

    public void saveSections(Long lineId, List<Section> sections) {
        String sql = "insert into SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) values (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Section section = sections.get(i);
                ps.setLong(1, lineId);
                ps.setLong(2, section.getUpStation().getId());
                ps.setLong(3, section.getDownStation().getId());
                ps.setInt(4, section.getDistance());
            }

            @Override
            public int getBatchSize() {
                return sections.size();
            }
        });
    }

    public void deleteSectionsOf(Long lineId) {
        String sql = "delete from SECTION where LINE_ID = ?";
        jdbcTemplate.update(sql, lineId);
    }

    public List<Section> findSections(Long lineId) {
        String sql = "select SEC.ID as ID, SEC.UP_STATION_ID as UP_STATION_ID, S1.name as UP_STATION_NAME, " +
                "SEC.DOWN_STATION_ID as DOWN_STATION_ID, S2.name as DOWN_STATION_NAME, " +
                "DISTANCE " +
                "from SECTION SEC " +
                "left outer join STATION S1 on SEC.up_station_id = S1.id " +
                "left outer join STATION S2 on SEC.down_station_id = S2.id " +
                "where SEC.LINE_ID = ?";
        return jdbcTemplate.query(sql, sectionRowMapper(), lineId);
    }
}
