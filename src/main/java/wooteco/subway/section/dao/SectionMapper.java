package wooteco.subway.section.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import wooteco.subway.section.dto.SectionResponse;

@Component
public class SectionMapper implements RowMapper<SectionResponse> {

    @Override
    public SectionResponse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        Long upStationId = resultSet.getLong("up_station_id");
        Long downStationId = resultSet.getLong("down_station_id");
        int distance = resultSet.getInt("distance");
        return new SectionResponse(id, upStationId, downStationId, distance);
    }
}
