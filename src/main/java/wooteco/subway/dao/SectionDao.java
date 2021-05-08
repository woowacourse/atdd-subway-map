package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Section section) {
        String query = "INSERT INTO SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = getPreparedStatementCreator(section, query);
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private PreparedStatementCreator getPreparedStatementCreator(Section section, String query) {
        return (connection) -> {
            PreparedStatement prepareStatement = connection.prepareStatement(query, new String[]{"id"});
            long upStationId = section.getUpwardStation().getId();
            long downStationId = section.getDownStation().getId();
            prepareStatement.setLong(1, section.getLineId());
            prepareStatement.setLong(2, upStationId);
            prepareStatement.setLong(3, downStationId);
            prepareStatement.setLong(4, section.getDistance());
            return prepareStatement;
        };
    }

    public List<Section> findAllByLineId(long lineId) {
        String query = "SELECT ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE FROM SECTION WHERE LINE_ID = ?";
        RowMapper<Section> rowMapper = (resultSet, rowNumber) -> {
            long id = resultSet.getLong("ID");
            long upStationId = resultSet.getLong("UP_STATION_ID");
            long downStationId = resultSet.getLong("DOWN_STATION_ID");
            int distance = resultSet.getInt("DISTANCE");
            Station upStation = findStationById(upStationId);
            Station downStation = findStationById(downStationId);
            return new Section(id, upStation, downStation, distance, lineId);
        };
        return jdbcTemplate.query(query, rowMapper, lineId);
    }

    private Station findStationById(long stationId) {
        String query = "SELECT NAME FROM STATION WHERE ID = ?";
        RowMapper<Station> ROW_MAPPER = (resultSet, rowNumber) -> {
            String name = resultSet.getString("name");
            return new Station(stationId, name);
        };
        return jdbcTemplate.queryForObject(query, ROW_MAPPER, stationId);
    }
}
