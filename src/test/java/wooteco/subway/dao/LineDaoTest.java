package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

public class LineDaoTest {

    @BeforeEach
    void beforEach() {
        LineDao.deleteAll();
    }

    @DisplayName("새 지하철 노선을 저장한다.")
    @Test
    void save() {
        Line testLine = new Line("save", "GREEN");
        Line result = LineDao.save(testLine);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("save");
        assertThat(result.getColor()).isEqualTo("GREEN");
    }

    @DisplayName("지하철 노선 이름을 이용해 지하철 노선을 조회한다.")
    @Test
    void findByName() {
        Line test = new Line("test", "GREEN");
        LineDao.save(test);
        Line result = LineDao.findByName("test").orElse(null);
        Optional<Line> result2 = LineDao.findByName("test2");

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getColor()).isEqualTo("GREEN");
        assertThat(result2).isEmpty();
    }

    @DisplayName("저장된 모든 지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Line test1 = new Line("test1", "GREEN");
        Line test2 = new Line("test2", "YELLOW");
        LineDao.save(test1);
        LineDao.save(test2);

        List<Line> lines = LineDao.findAll();

        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines.get(0).getId()).isEqualTo(1);
        assertThat(lines.get(0).getName()).isEqualTo("test1");
        assertThat(lines.get(0).getColor()).isEqualTo("GREEN");
        assertThat(lines.get(1).getId()).isEqualTo(2);
        assertThat(lines.get(1).getName()).isEqualTo("test2");
        assertThat(lines.get(1).getColor()).isEqualTo("YELLOW");
    }

    @DisplayName("id를 이용해 지하철 노선을 조회한다.")
    @Test
    void findById() {
        LineDao.save(new Line("test1", "GREEN"));
        Line line = LineDao.findById(1L).get();
        Optional<Line> lineEmpty = LineDao.findById(1000L);
        assertThat(line.getId()).isEqualTo(1);
        assertThat(line.getName()).isEqualTo("test1");
        assertThat(line.getColor()).isEqualTo("GREEN");
        assertThat(lineEmpty).isEmpty();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        Line test = new Line("test1", "YELLOW");
        Line savedTest = LineDao.save(test);
        Line test2 = new Line("test2", "BROWN");
        LineDao.save(test2);

        LineDao.delete(savedTest);

        List<Line> result = LineDao.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        Line line = new Line("test1", "YELLOW");
        Line savedLine = LineDao.save(line);
        Line newLine = new Line(savedLine.getId(), "test2", "BROWN");

        LineDao.update(savedLine, newLine);

        Line result = LineDao.findById(1L).get();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("test2");
        assertThat(result.getColor()).isEqualTo("BROWN");

    }

    @AfterAll
    static void afterAll() {
        LineDao.deleteAll();
    }
}
