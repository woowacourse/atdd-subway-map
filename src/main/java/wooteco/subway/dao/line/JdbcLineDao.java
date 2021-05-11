package wooteco.subway.dao.line;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.common.PersistenceUtils;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Repository
@RequiredArgsConstructor
public class JdbcLineDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SectionDao sectionDao;

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) -> {
            Long foundId = rs.getLong("id");
            final String color = rs.getString("color");
            final String name = rs.getString("name");
            return Line.create(foundId, name, color);
        };
    }

    @Override
    public Optional<Line> findLineByName(String name) {
        final String sql = "SELECT * FROM line WHERE name = ?";
        return jdbcTemplate.query(sql, lineRowMapper(), name)
            .stream()
            .findAny();
    }

    @Override
    public Line save(Line line) {
        final String sql = "INSERT INTO line(name, color) VALUES(?, ?)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            final PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, line.getName());
            preparedStatement.setString(2, line.getColor());
            return preparedStatement;
        }, keyHolder);
        final long lineId = keyHolder.getKey().longValue();
        PersistenceUtils.insertId(line, lineId);

        final Section section = line.firstSection();
        sectionDao.save(section, lineId);

        return line;
    }

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, lineRowMapper());
    }

    @Override
    public Optional<Line> findCompleteLineById(Long id) {
        final String sql = "SELECT * FROM line WHERE id = ?";
        final Optional<Line> foundLine = jdbcTemplate.query(sql, lineRowMapper(), id).stream().findAny();
        foundLine.ifPresent(line -> line.addSections(Sections.create(sectionDao.findAllByLineId(id))));
        return foundLine;
    }

    @Override
    public void removeLine(Long id) {
        String sql = "DELETE FROM line WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Line line) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
    }

    @Override
    public Optional<Line> findLineByNameOrColor(String name, String color, Long lineId) {
        String sql = "SELECT * FROM line where (name = ? OR color = ?) AND id != ?";
        return jdbcTemplate.query(sql, lineRowMapper(), name, color, lineId).stream().findAny();
    }
}
