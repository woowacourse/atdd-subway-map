package wooteco.subway.dao;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Section;

@Repository
public class SectionDao {
    /*
    id              BIGINT AUTO_INCREMENT NOT NULL,
    line_id         BIGINT                NOT NULL,
    up_station_id   BIGINT                NOT NULL,
    down_station_id BIGINT                NOT NULL,
    distance        INT                   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (line_id) REFERENCES line (id),
    FOREIGN KEY (up_station_id) REFERENCES station (id),
    FOREIGN KEY (down_station_id) REFERENCES station (id)
     */

    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public SectionDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    public Section save(Section section, Long lineId) {
        final long sectionId = jdbcInsert.executeAndReturnKey(Map.of(
            "line_id", lineId,
            "up_station_id", section.getUpStation().getId(),
            "down_station_id", section.getDownStation().getId(),
            "distance", section.getDistance().getValue()
        )).longValue();
        return new Section(sectionId, section.getUpStation(), section.getDownStation(), section.getDistance());
    }
}
