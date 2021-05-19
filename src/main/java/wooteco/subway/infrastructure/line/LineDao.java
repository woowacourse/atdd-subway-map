package wooteco.subway.infrastructure.line;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.line.LineColor;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.line.LineName;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Line save(Line line) {
        final String sql = "INSERT INTO LINE (name, color) VALUES (?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1, line.getLineName());
            pstmt.setString(2, line.getLineColor());

            return pstmt;
        }, keyHolder);

        return new Line(
                new LineId(keyHolder.getKeyAs(Long.class)),
                new LineName(line.getLineName()),
                new LineColor(line.getLineColor()),
                new Sections(line.getSections())
        );
    }

    public List<Line> allLines() {
        final String sql = "SELECT * FROM LINE";

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            final long id = resultSet.getLong("id");
            final String name = resultSet.getString("name");
            final String color = resultSet.getString("color");

            return new Line(
                    new LineId(id),
                    new LineName(name),
                    new LineColor(color)
            );
        });
    }

    public Line findById(final Long id) {
        final String sql = "SELECT * FROM LINE WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            final String name = rs.getString("name");
            final String color = rs.getString("color");

            return new Line(
                    new LineId(id),
                    new LineName(name),
                    new LineColor(color)
            );
        }, id);
    }

    public void update(final Line line) {
        final String sql = "UPDATE LINE SET name = ?, color = ? WHERE id = ?";

        jdbcTemplate.update(sql, line.getLineName(), line.getLineColor(), line.getLineId());
    }

    public void deleteById(final Long id) {
        final String sql = "DELETE FROM LINE WHERE id = ?";

        jdbcTemplate.update(sql, id);
    }

    public boolean contains(Line line) {
        final String sql = "SELECT count(*) AS count FROM LINE WHERE name = ? AND color = ?";

        String lineName = line.getLineName();
        String lineColor = line.getLineColor();

        Long count = jdbcTemplate.queryForObject(
                sql, (rs, rowNum) -> rs.getLong("count"), lineName, lineColor
        );

        Objects.requireNonNull(count);

        return !count.equals(0L);
    }
}
