package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        Line line = new Line("2호선", "green");
        Line savedLine = lineDao.save(line);

        assertThat(line.getName()).isEqualTo(savedLine.getName());
    }

//    @DisplayName("같은 이름의 노선을 저장하는 경우 예외가 발생한다.")
//    @Test
//    void saveExistingName() {
//        Line line = new Line("2호선", "green");
//        lineDao.save(line);
//
//        assertThatThrownBy(() -> {
//            lineDao.save(line);
//        }).isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("같은 이름의 노선은 등록할 수 없습니다.");
//    }

    @DisplayName("모든 지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Line line1 = new Line("2호선", "green");
        Line line2 = new Line("3호선", "orange");
        Line line3 = new Line("8호선", "pink");
        lineDao.save(line1);
        lineDao.save(line2);
        lineDao.save(line3);

        assertThat(lineDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("id로 지하철 노선을 조회한다.")
    @Test
    void findById() {
        Line line = new Line("2호선", "green");
        Line savedLine = lineDao.save(line);

        Optional<Line> foundLine = lineDao.findById(savedLine.getId());

        assertThat(foundLine.get().getName()).isEqualTo(savedLine.getName());
    }

    @DisplayName("name으로 지하철 노선을 조회한다.")
    @Test
    void findByName() {
        Line line = new Line("2호선", "green");
        Line savedLine = lineDao.save(line);

        Optional<Line> foundLine = lineDao.findByName(savedLine.getName());

        assertThat(foundLine.get().getColor()).isEqualTo(savedLine.getColor());
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회할 경우 Optional.empty로 반환한다.")
    @Test
    void findNotExistingLine() {
        Optional<Line> foundLine = lineDao.findById(1L);

        assertThat(foundLine.isPresent()).isFalse();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        Line line = new Line("2호선", "green");
        Line savedLine = lineDao.save(line);

        Line updatedLine = lineDao.update(savedLine.getId(), "3호선", "orange");

        assertThat(updatedLine.getName()).isEqualTo("3호선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteById() {
        Line line = new Line("2호선", "green");
        Line savedLine = lineDao.save(line);

        lineDao.deleteById(savedLine.getId());

        assertThat(lineDao.findAll().size()).isZero();
    }

    @DisplayName("존재하지 않는 노선을 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteNotExistingLine() {
        assertThatThrownBy(() -> lineDao.deleteById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }
}
