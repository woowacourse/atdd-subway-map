package wooteco.subway.dao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Repository
public class JdbcSectionDao implements SectionDao {
    private final SimpleJdbcInsert jdbcInsert;
    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Section section, Long lineId) {
        save(section, lineId, 0);
    }

    private void save(Section section, Long lineId, int index) {
        Map<String, Object> param = new HashMap<>();
        param.put("line_id", lineId);
        param.put("up_station_id", section.getUpStationId());
        param.put("down_station_id", section.getDownStationId());
        param.put("distance", section.getDistance());
        param.put("index_num", index);
        jdbcInsert.execute(param);
    }

    @Override
    public void save(Sections sections, Long lineId) {
        LinkedList<Section> values = sections.getSections();
        for (int i = 0; i < values.size(); i++) {
            Section section = values.get(i);
            update(section, lineId, i);
        }
    }

    private void update(Section section, Long lineId, int index) {
        if (Objects.isNull(section.getId())) {
            save(section, lineId, index);
            return;
        }
        String sql = "UPDATE section SET index_num = ? WHERE id = ?";
        jdbcTemplate.update(sql, index, section.getId());
    }

    @Override
    public void deleteByLine(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(sql, lineId);
    }
}
