package wooteco.subway.section;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.NoSuchDataException;

@Repository
public class SectionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public long save(Section section) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, section.getLineId());
            ps.setLong(2, section.getUpStationId());
            ps.setLong(3, section.getDownStationId());
            ps.setInt(4, section.getDistance());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Section findByLineId(long lineId) {
        String query = "SELECT * FROM section WHERE id = ?";
        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
            Section section = new Section(
                resultSet.getLong("line_id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
            );
            return section;
        }, lineId);
    }

    public List<RouteInSection> findStationsByLineId(long lineId) {
        String query = "SELECT up_station_id, down_station_id FROM section WHERE line_id = (?)";

        return jdbcTemplate.query(query, (resultSet, rowNum) -> {
            return new RouteInSection(
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id")
            );
        }, lineId);
    }

    public void delete(Long stationId) {
        String query = "DELETE FROM section WHERE ID = ?";
        int affectedRowNumber = jdbcTemplate.update(query, stationId);

        if (affectedRowNumber == 0) {
            throw new NoSuchDataException("없는 구간입니다.");
        }
    }
}
