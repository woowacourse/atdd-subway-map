package wooteco.subway.section;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SectionEntity save(SectionEntity sectionEntity) {
        String sql = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sectionEntity.getLineId());
            ps.setLong(2, sectionEntity.getUpStationId());
            ps.setLong(3, sectionEntity.getDownStationId());
            ps.setLong(4, sectionEntity.getDistance());
            return ps;
        }, keyHolder);

        return new SectionEntity(Objects.requireNonNull(keyHolder.getKey()).longValue(), sectionEntity);
    }
}
