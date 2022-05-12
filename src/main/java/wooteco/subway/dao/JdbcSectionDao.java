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
        String sql = "UPDATE section "
                + "SET up_station_id = ?, down_station_id = ?, distance = ?, index_num = ? "
                + "WHERE id = ?";
        jdbcTemplate.update(sql,
                section.getUpStationId(), section.getDownStationId(), section.getDistance(), index,
                section.getId());
    }

    @Override
    public int delete(Section section) {
        String sql = "DELETE FROM section WHERE id = ?";
        return delete(sql, section.getId());
    }

    @Override
    public int deleteByLine(Long lineId) {
        String sql = "DELETE FROM section WHERE line_id = ?";
        return delete(sql, lineId);
    }

    private int delete(String sql, Long id) {
        int deletedCount = jdbcTemplate.update(sql, id);
        validateRemoved(deletedCount);
        return deletedCount;
    }

    private void validateRemoved(int count) {
        if (count == 0) {
            throw new IllegalStateException("삭제할 구간이 존재하지 않습니다.");
        }
    }
}
