package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("ID");
    }

    public long save(Section section) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(section);
        return simpleJdbcInsert.executeAndReturnKey(sqlParameterSource)
                .longValue();
    }

    public List<Section> findAllByLineId(long lineId) {
        String query = "SELECT ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE FROM SECTION WHERE LINE_ID = ?";
        RowMapper<Section> sectionRowMapper = getSectionRowMapper(lineId);
        return jdbcTemplate.query(query, sectionRowMapper, lineId);
    }

    private RowMapper<Section> getSectionRowMapper(long lineId) {
        return (resultSet, rowNumber) -> {
            long id = resultSet.getLong("ID");
            long upStationId = resultSet.getLong("UP_STATION_ID");
            long downStationId = resultSet.getLong("DOWN_STATION_ID");
            int distance = resultSet.getInt("DISTANCE");
            Station upStation = findStationById(upStationId);
            Station downStation = findStationById(downStationId);
            return new Section(id, upStation, downStation, distance, lineId);
        };
    }

    private Station findStationById(long stationId) {
        String query = "SELECT NAME FROM STATION WHERE ID = ?";
        RowMapper<Station> stationRowMapper = (resultSet, rowNumber) -> {
            String name = resultSet.getString("name");
            return new Station(stationId, name);
        };
        return jdbcTemplate.queryForObject(query, stationRowMapper, stationId);
    }
}
