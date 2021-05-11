package wooteco.subway.section;

import java.sql.PreparedStatement;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public long save(SectionAddDto sectionAddDto) {
        String query = "INSERT INTO section(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setLong(1, sectionAddDto.getLineId());
            ps.setLong(2, sectionAddDto.getUpStationId());
            ps.setLong(3, sectionAddDto.getDownStationId());
            ps.setInt(4, sectionAddDto.getDistance());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
