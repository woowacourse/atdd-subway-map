package wooteco.subway.dao.section;

import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.common.PersistenceUtils;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Repository
@RequiredArgsConstructor
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Section save(Section section, Long lineId) {
        final String sql = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            final PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setLong(1, lineId);
            preparedStatement.setLong(2, section.upStationId());
            preparedStatement.setLong(3, section.downStationId());
            preparedStatement.setInt(4, section.getDistance());
            return preparedStatement;
        }, keyHolder);
        final long sectionId = keyHolder.getKey().longValue();
        PersistenceUtils.insertId(section, sectionId);
        return section;
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT id, (SELECT name FROM station WHERE station.id = section.up_station_id) AS upStationName, up_station_id AS upStationId, "
            + "(SELECT name FROM station WHERE station.id = section.down_station_id) AS downStationName, down_station_id AS downStationId, "
            + "distance FROM SECTION WHERE line_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            final long id = rs.getLong("id");

            final long upStationId = rs.getLong("upStationId");
            final String upStationName = rs.getString("upStationName");
            final Station upStation = Station.create(upStationId, upStationName);

            final long downStationId = rs.getLong("downStationId");
            final String downStationName = rs.getString("downStationName");
            final Station downStation = Station.create(downStationId, downStationName);

            final int distance = rs.getInt("distance");

            return Section.create(id, upStation, downStation, distance);
        }, lineId);
    }

    @Override
    public void update(Section section) {
        String sql = "UPDATE section SET up_station_id=?, down_station_id=? where id=? ";
        jdbcTemplate.update(sql, section.upStationId(), section.downStationId(), section.getId());
    }
}
