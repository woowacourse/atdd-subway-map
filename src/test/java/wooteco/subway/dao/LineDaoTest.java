package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setup() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("노선을 저장하면 저장된 노선 정보를 반환한다.")
    void save() {
        // given
        final Line line = new Line("7호선", "bg-red-600");

        // when
        Line savedLine = lineDao.save(line);

        // then
        assertThat(savedLine.getName()).isEqualTo(line.getName());
        assertThat(savedLine.getColor()).isEqualTo(line.getColor());
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void findAll() {
        // given
        String line7Name = "7호선";
        String line7Color = "bg-red-600";
        final Line line7 = new Line(line7Name, line7Color);
        lineDao.save(line7);

        final String line5Name = "5호선";
        final String line5Color = "bg-green-600";
        final Line line5 = new Line(line5Name, line5Color);
        lineDao.save(line5);

        // when
        final List<Line> lines = lineDao.findAll();
        List<String> names = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());

        // then
        assertThat(lines.size()).isEqualTo(2);
        assertThat(names).containsOnly(line5Name, line7Name);
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 조회한다.")
    void findById() {
        // given
        final Line line7 = new Line("7호선", "bg-red-600");
        Line persistLine = lineDao.save(line7);

        // when
        Line actual = lineDao.findById(persistLine.getId()).get();

        // then
        assertAll(() -> {
            assertThat(actual.getName()).isEqualTo(persistLine.getName());
            assertThat(actual.getColor()).isEqualTo(persistLine.getColor());
        });
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 업데이트한다.")
    void updateById() {
        // given
        final Line persistLine = lineDao.save(new Line("7호선", "bg-red-600"));

        // when
        final Line lineForUpdate = new Line(persistLine.getId(), "5호선", "bg-green-600");
        final Line updatedLine = lineDao.update(lineForUpdate).get();

        // then
        assertAll(() -> {
            assertThat(lineForUpdate.getName()).isEqualTo(updatedLine.getName());
            assertThat(lineForUpdate.getColor()).isEqualTo(updatedLine.getColor());
        });
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 삭제한다.")
    void deleteById() {
        // given
        final Line persistLine = lineDao.save(new Line("7호선", "bg-red-600"));

        // when
        Long id = persistLine.getId();
        Integer affectedRows = lineDao.deleteById(id);

        // then
        assertThat(affectedRows).isOne();
        assertThat(lineDao.findAll()).hasSize(0);
    }
}
