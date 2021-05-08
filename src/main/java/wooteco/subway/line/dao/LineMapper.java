package wooteco.subway.line.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import wooteco.subway.line.Line;

@Component
public class LineMapper implements RowMapper<Line> {

    @Override
    public Line mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String color = resultSet.getString("color");
        return new Line(id, name, color);
    }
}