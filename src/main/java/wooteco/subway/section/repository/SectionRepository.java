package wooteco.subway.section.repository;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.domain.Station;

@Repository
public class SectionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section) {
        String sql = "INSERT INTO SECTION (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement prepareStatement = con.prepareStatement(sql, new String[]{"id"});
            prepareStatement.setLong(1, section.getLineId());
            prepareStatement.setLong(2, section.getUpStationId());
            prepareStatement.setLong(3, section.getDownStationId());
            prepareStatement.setInt(4, section.getDistance());
            return prepareStatement;
        }, keyHolder);

        return createNewObject(section, Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    private Section createNewObject(Section section, Long id) {
        Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, id);
        return section;
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT *,\n"
            + "(SELECT name AS up_station_name\n"
            + "FROM SECTION \n"
            + "INNER JOIN STATION\n"
            + "ON section.up_station_id = station.id\n"
            + "WHERE SECTION.id = ORIGIN_SECTION_TB.id) AS up_station_name,\n"
            + "(SELECT name AS up_station_name\n"
            + "FROM SECTION \n"
            + "INNER JOIN STATION\n"
            + "ON section.down_station_id = station.id\n"
            + "WHERE SECTION.id = ORIGIN_SECTION_TB.id) AS down_station_name,\n"
            + "FROM SECTION as ORIGIN_SECTION_TB WHERE line_id = ?";

        return jdbcTemplate.query(sql, (resultSet, rowNumber) ->
            new Section(
                resultSet.getLong("id"),
                resultSet.getLong("line_id"),
                new Station(resultSet.getLong("up_station_id"),
                    resultSet.getString("up_station_name")),
                new Station(resultSet.getLong("down_station_id"),
                    resultSet.getString("down_station_name")),
                resultSet.getInt("distance")
            ), lineId);
    }

    public Integer delete(Section section) {
        String sql = "DELETE FROM SECTION WHERE id = ?";
        return jdbcTemplate.update(sql, section.getId());
    }

}
