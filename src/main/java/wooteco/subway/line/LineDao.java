package wooteco.subway.line;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.Sections;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SectionDao sectionDao;

    public LineDao(JdbcTemplate jdbcTemplate, SectionDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionDao = sectionDao;
    }

    private RowMapper<Line> lineRowMapper() {
        return (resultSet, rowNum) -> new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color")
        );
    }

    public Line save(String name, String color) {
        String sql = "insert into Line (name, color) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, name);
            ps.setString(2, color);
            return ps;
        }, keyHolder);
        return new Line(keyHolder.getKey()
                .longValue(), name, color);
    }

    public Optional<Line> findById(Long id) {
        String sql = "select id, name, color from LINE where id = ?";
        try {
            Line line = jdbcTemplate.queryForObject(sql, lineRowMapper(), id);
            line.setSections(new Sections(sectionDao.findSectionsByLineId(id)));
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Line> findByName(String name) {
        String sql = "select id, name, color from LINE where name = ?";
        try {
            Line line = jdbcTemplate.queryForObject(sql, lineRowMapper(), name);
            line.setSections(new Sections(sectionDao.findSectionsByLineId(line.getId())));
            return Optional.ofNullable(line);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from LINE";
        List<Line> lines = jdbcTemplate.query(sql, lineRowMapper());
        for (Line line : lines) {
            line.setSections(new Sections(sectionDao.findSectionsByLineId(line.getId())));
        }
        return lines;
    }

    public int update(Long id, String name, String color) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        return jdbcTemplate.update(sql, name, color, id);
    }

    public int delete(Long id) {
        String sql = "delete from LINE where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
