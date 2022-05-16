package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import java.util.List;
import java.util.stream.Collectors;

@JdbcTest
class LineDaoTest {

    private final LineDao lineDao;

    @Autowired
    private LineDaoTest(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.lineDao = new LineDao(namedParameterJdbcTemplate);
    }

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = lineDao.save(line);

        assertThat(savedLine).isNotNull();
    }

    @DisplayName("모든 노선을 불러온다.")
    @Test
    void findAll() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-green-600");

        lineDao.save(line1);
        lineDao.save(line2);

        final List<Line> lines = lineDao.findAll();
        final List<String> lineNames = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        final List<String> lineColors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(lineNames).containsAll(List.of("신분당선", "분당선")),
                () -> assertThat(lineColors).containsAll(List.of("bg-red-600", "bg-green-600"))
        );
    }

    @DisplayName("아이디로 노선 하나를 불러온다.")
    @Test
    void findById() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line expected = lineDao.save(line1);
        final Line actual = lineDao.findById(expected.getId()).get();

        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(expected.getColor())
        );
    }

    @DisplayName("노선을 변경한다.")
    @Test
    void update() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line savedLine = lineDao.save(line1);
        final Long savedId = savedLine.getId();

        final String newLineName = "새로운 노선";
        final String newLineColor = "bg-red-500";
        final Line newLine = new Line(newLineName, newLineColor);
        lineDao.update(savedId, newLine);

        assertAll(
                () -> assertThat(lineDao.findById(savedId)).isPresent(),
                () -> assertThat(lineDao.findById(savedId).get().getName()).isEqualTo(newLineName),
                () -> assertThat(lineDao.findById(savedId).get().getColor()).isEqualTo(newLineColor)
        );
    }

    @DisplayName("아이디로 노선을 삭제한다.")
    @Test
    void deleteById() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line savedLine = lineDao.save(line1);

        lineDao.deleteById(savedLine.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }
}
