package wooteco.subway.line.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import wooteco.subway.line.Line;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 jdbc 테스트")
@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("classpath:application-test.yml")
class JdbcLineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcLineDao jdbcLineDao;

    @BeforeEach
    void setUp() {
        this.jdbcLineDao = new JdbcLineDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선 생성")
    @Test
    void save() {
        // given
        Line 분당선 = new Line("분당선", "red");

        // when
        Line line = jdbcLineDao.save(분당선);

        // then
        assertThat(line).usingRecursiveComparison()
                .isEqualTo(new Line(1L, "분당선", "red"));
    }

    @DisplayName("모든 지하철 노선 조회")
    @Test
    void findAll() {
        // given
        jdbcLineDao.save(new Line("분당선", "red"));
        jdbcLineDao.save(new Line("신분당선", "yellow"));
        jdbcLineDao.save(new Line("2호선", "green"));

        // when
        List<Line> lines = jdbcLineDao.findAll();

        // then
        assertThat(lines).hasSize(3);
        assertThat(lines).usingRecursiveFieldByFieldElementComparator()
                .containsAll(Arrays.asList(
                        new Line(1L, "분당선", "red"),
                        new Line(2L, "신분당선", "yellow"),
                        new Line(3L, "2호선", "green")
                ));
    }

    @DisplayName("Id로 지하철 노선 조회")
    @Test
    void findById() {
        // given
        Long id = 1L;
        jdbcLineDao.save(new Line("분당선", "red"));

        // when
        Optional<Line> line = jdbcLineDao.findById(id);

        // then
        assertThat(line.get()).usingRecursiveComparison()
                .isEqualTo(new Line(id, "분당선", "red"));
    }

    @DisplayName("이름으로 지하철 노선 조회")
    @Test
    void findByName() {
        // given
        String 분당선 = "분당선";
        jdbcLineDao.save(new Line(분당선, "red"));

        // when
        Optional<Line> line = jdbcLineDao.findByName(분당선);

        // then
        assertThat(line.get()).usingRecursiveComparison()
                .isEqualTo(new Line(1L, 분당선, "red"));
    }

    @DisplayName("지하철 노선 정보 수정")
    @Test
    void update() {
        // given
        jdbcLineDao.save(new Line("분당선", "red"));

        // when
        jdbcLineDao.update(new Line(1L, "2호선", "green"));

        // then
        assertThat(jdbcLineDao.findById(1L).get()).usingRecursiveComparison()
                .isEqualTo(new Line(1L, "2호선", "green"));
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void delete() {
        // given
        jdbcLineDao.save(new Line("분당선", "red"));
        int originalSize = jdbcLineDao.findAll().size();

        // when
        jdbcLineDao.delete(1L);

        // then
        assertThat(jdbcLineDao.findAll().size()).isEqualTo(originalSize - 1);
    }

    @DisplayName("원래 이름을 제외하고 이름으로 지하철 노선 이름 조회")
    @Test
    void findByNameAndNotInOriginalName() {
        // given
        String 분당선 = "분당선";
        jdbcLineDao.save(new Line(분당선, "red"));

        // when
        Optional<String> searchOtherName = jdbcLineDao.findByNameAndNotInOriginalName(분당선, "2호선");
        Optional<String> searchOriginalName = jdbcLineDao.findByNameAndNotInOriginalName(분당선, 분당선);

        // then
        assertThat(searchOtherName.isPresent()).isTrue();
        assertThat(searchOriginalName.isPresent()).isFalse();
    }
}