package wooteco.subway.dao.station;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.station.Station;

@Component
public class StationMapper implements RowMapper<Station> {

    @Override
    public Station mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Station(id, name);
    }
}
