package wooteco.subway.dao.section;

import java.sql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.common.PersistenceUtils;
import wooteco.subway.domain.Section;

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
}
