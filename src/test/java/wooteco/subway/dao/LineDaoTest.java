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
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);

        lineDao.save(new Line("신분당선", "red"));
        lineDao.save(new Line("짱분당선", "blue"));
        lineDao.save(new Line("구분당선", "green"));
    }

    @Test
    @DisplayName("노선을 등록한다.")
    void saveTest() {
        final Line line = lineDao.save(new Line("라쿤선", "black"));
        assertAll(
            () -> assertThat(line.getId()).isEqualTo(4L),
            () -> assertThat(line.getName()).isEqualTo("라쿤선"),
            () -> assertThat(line.getColor()).isEqualTo("black")
        );
    }

    @Test
    @DisplayName("정상적으로 전체 조회되는 경우를 테스트한다.")
    void findAllTest() {
        List<String> lines = lineDao.findAll()
            .stream()
            .map(Line::getName)
            .collect(Collectors.toList());

        assertAll(
            () -> assertThat(lines).hasSize(3),
            () -> assertThat(lines).containsAll(List.of("신분당선", "짱분당선", "구분당선"))
        );
    }

    @Test
    @DisplayName("ID로 특정 노선을 조회한다.")
    void findByIdTest() {
        final Line 라쿤선 = lineDao.save(new Line("라쿤선", "black"));
        final Line line = lineDao.findById(라쿤선.getId()).get();

        assertAll(
            () -> assertThat(line.getId()).isEqualTo(라쿤선.getId()),
            () -> assertThat(line.getName()).isEqualTo("라쿤선"),
            () -> assertThat(line.getColor()).isEqualTo("black")
        );
    }

    @Test
    @DisplayName("ID로 특정 노선을 수정한다.")
    void updateTest() {
        final Line 짱구선 = lineDao.save(new Line("짱구선", "white"));
        lineDao.update(짱구선.getId(), new Line("38선", "rainbow"));
        Line line = lineDao.findById(짱구선.getId()).get();

        assertAll(
            () -> assertThat(line.getName()).isEqualTo("38선"),
            () -> assertThat(line.getColor()).isEqualTo("rainbow")
        );
    }

    @Test
    @DisplayName("ID로 특정 노선을 삭제한다.")
    void deleteTest() {
        final Line 짱구선 = lineDao.save(new Line("짱구선", "white"));
        lineDao.deleteById(짱구선.getId());

        assertThat(lineDao.findById(짱구선.getId())).isEmpty();
    }
}
