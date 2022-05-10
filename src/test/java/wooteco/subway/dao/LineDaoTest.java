package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDaoImpl lineDao;

    @BeforeEach
    void beforeEach() {
        lineDao = new LineDaoImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 노선을 저장할 수 있다.")
    void insert() {
        String name = "신분당선";
        String color = "bg-red-600";
        Line line = lineDao.insert(new Line(name, color));

        assertThat(line.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("id로 지하철 노선을 조회할 수 있다.")
    void findById() {
        String name = "신분당선";
        String color = "bg-red-600";
        Line line = lineDao.insert(new Line(name, color));

        Line foundLine = lineDao.findById(line.getId());
        assertThat(foundLine.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("이미 존재하는 이름의 노선인지 확인한다.")
    void existByName() {
        lineDao.insert(new Line("신분당선", "bg-red-600"));

        Boolean actual = lineDao.existByName(new Line("신분당선", "bg-green-600"));
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("이미 존재하는 색의 노선인지 확인한다.")
    void existByColor() {
        lineDao.insert(new Line("신분당선", "bg-red-600"));

        Boolean actual = lineDao.existByColor(new Line("분당선", "bg-red-600"));
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("지하철 노선들을 조회할 수 있다.")
    void findAll() {
        lineDao.insert(new Line("신분당선", "bg-red-600"));
        lineDao.insert(new Line("2호선", "bg-blue-500"));

        List<Line> lines = lineDao.findAll();
        assertThat(lines.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선을 업데이트할 수 있다.")
    void update() {
        Line line = lineDao.insert(new Line("신분당선", "bg-red-600"));

        Long id = line.getId();
        String updateName = "2호선";
        String updateColor = "bg-blue-500";
        lineDao.update(id, updateName, updateColor);

        Line updatedLine = lineDao.findById(id);
        Assertions.assertAll(
                () -> assertThat(updatedLine.getName()).isEqualTo(updateName),
                () -> assertThat(updatedLine.getColor()).isEqualTo(updateColor)
        );
    }

    @Test
    @DisplayName("지하철 노선을 지울 수 있다.")
    void delete() {
        Line line = lineDao.insert(new Line("신분당선", "bg-red-600"));

        lineDao.delete(line.getId());
        assertThat(lineDao.findAll()).isEmpty();
    }
}
