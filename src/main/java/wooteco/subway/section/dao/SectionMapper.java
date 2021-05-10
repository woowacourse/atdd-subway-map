package wooteco.subway.section.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import wooteco.subway.section.Section;

@Component
public class SectionMapper implements RowMapper<Section> {

    @Override
    public Section mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        Long lineId = resultSet.getLong("line_id");
        Long upStationId = resultSet.getLong("up_station_id");
        Long downStationId = resultSet.getLong("down_station_id");
        int distance = resultSet.getInt("distance");
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

}
