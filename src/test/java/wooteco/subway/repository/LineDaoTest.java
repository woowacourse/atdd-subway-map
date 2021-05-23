package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exception.LineNotFoundException;

@JdbcTest
@DisplayName("노선 레포지토리 레이어 테스트")
class LineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 노선을 생성 및 저장한다.")
    void save() {
        // given
        Line line = new Line("2호선", "green");

        // when
        long id = lineDao.save(line);

        Line savedLine = lineDao.findById(id)
            .orElseThrow(LineNotFoundException::new);

        // then
        assertThat(savedLine.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("생성된 노선들을 불러온다.")
    void findAll() {
        // given
        Line line1 = new Line("2호선", "green");
        Line line2 = new Line("3호선", "green");
        lineDao.save(line1);
        lineDao.save(line2);

        // when
        List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("특정 노선을 아이디로 찾아온다.")
    void findById() {
        // given
        Line line = new Line("2호선", "green");
        Long id = lineDao.save(line);

        // when
        Line line2 = lineDao.findById(id)
            .orElseThrow(LineNotFoundException::new);

        // then
        assertThat(line2.getName()).isEqualTo(line.getName());
        assertThat(line2.getColor()).isEqualTo(line.getColor());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 찾아올 때 에러가 발생한다.")
    void cannotFindById() {
        assertThatThrownBy(() -> {
            lineDao.findById(Long.MAX_VALUE)
                .orElseThrow(LineNotFoundException::new);
        }).isInstanceOf(LineNotFoundException.class);
    }


    @Test
    @DisplayName("노선 정보를 수정한다.")
    void updateLine() {
        // given
        Line line1 = new Line("2호선", "green");
        long id = lineDao.save(line1);

        // when
        Line line2 = new Line(id, "3호선", "red");
        lineDao.updateLine(line2);
        Line line3 = lineDao.findById(id)
            .orElseThrow(LineNotFoundException::new);

        // then
        assertThat(line3.getName()).isEqualTo(line2.getName());
        assertThat(line3.getColor()).isEqualTo(line2.getColor());
    }

    @Test
    @DisplayName("등록된 노선을 제거한다.")
    void deleteById() {
        // given
        Line line = new Line("2호선", "green");
        Long id = lineDao.save(line);

        // when
        lineDao.deleteById(id);


        // then
        assertThatThrownBy(() -> {
            lineDao.findById(id)
                .orElseThrow(LineNotFoundException::new);
        }).isInstanceOf(LineNotFoundException.class);
    }
}