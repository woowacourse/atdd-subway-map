package wooteco.subway.line.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.infra.JdbcLineRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode;

@DataJdbcTest
@TestConstructor(autowireMode = AutowireMode.ALL)
class JdbcLineRepositoryTest {

    private final LineRepository lineRepository;
    private Line featureLine;

    public JdbcLineRepositoryTest(JdbcTemplate jdbcTemplate) {
        this.lineRepository = new JdbcLineRepository(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        Line line = new Line("2호선", "bg-red-600");
        this.featureLine = lineRepository.save(line);
    }

    @Test
    void save() {
        //given
        String expectedName = "3호선";
        String expectedColor = "bg-blue-500";
        Line line = new Line(expectedName, expectedColor);

        //when
        Line resultLine = lineRepository.save(line);
        String resultName = resultLine.getName();
        String resultColor = resultLine.getColor();

        //then
        assertThat(expectedName).isEqualTo(resultName);
        assertThat(expectedColor).isEqualTo(resultColor);
    }

    @Test
    void findAll() {
        //given
        String expectedName = "3호선";
        String expectedColor = "bg-blue-500";
        Line line = new Line(expectedName, expectedColor);
        lineRepository.save(line);

        //when
        List<Line> lines = lineRepository.findAll();

        //then
        assertThat(lines).hasSize(2);
    }

    @Test
    void findById() {
        //given
        String expectedName = "3호선";
        String expectedColor = "bg-blue-500";
        Line line = new Line(expectedName, expectedColor);
        Line expectedLine = lineRepository.save(line);
        Long expectedId = expectedLine.getId();

        //when
        Line resultLine = lineRepository.findById(expectedId).get();
        Long resultId = resultLine.getId();
        String resultName = resultLine.getName();
        String resultColor = resultLine.getColor();

        //then
        assertThat(expectedId).isEqualTo(resultId);
        assertThat(expectedName).isEqualTo(resultName);
        assertThat(expectedColor).isEqualTo(resultColor);
    }

    @Test
    void delete() {
        //when
        lineRepository.delete(featureLine.getId());
        List<Line> lines = lineRepository.findAll();

        //then
        assertThat(lines).hasSize(0);
    }

    @Test
    void update() {
        //given
        Long expectedId = featureLine.getId();
        String expectedName = "200호선";
        String expectedColor = "bg-blue-999";
        Line updateLine = new Line(expectedId, expectedName, expectedColor);

        //when
        lineRepository.update(updateLine);
        Line resultLine = lineRepository.findById(expectedId).get();
        Long resultId = resultLine.getId();
        String resultName = resultLine.getName();
        String resultColor = resultLine.getColor();

        //then
        assertThat(expectedId).isEqualTo(resultId);
        assertThat(expectedName).isEqualTo(resultName);
        assertThat(expectedColor).isEqualTo(resultColor);
    }

    @Test
    void deleteAll() {
        //when
        lineRepository.deleteAll();
        List<Line> lines = lineRepository.findAll();

        //then
        assertThat(lines).hasSize(0);
    }
}