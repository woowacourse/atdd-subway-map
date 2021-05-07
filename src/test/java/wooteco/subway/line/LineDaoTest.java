package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Rollback
class LineDaoTest {
    LineDao lineDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("노선 추가")
    void insert() {
        // when
        Line 코다선 = lineDao.insert(new Line("bg-red-100", "코다선"));

        // then
        assertThat(코다선.getColor()).isEqualTo("bg-red-100");
        assertThat(코다선.getName()).isEqualTo("코다선");
    }

    @Test
    @DisplayName("노선 목록 조회")
    void findAll() {
        //given
        Line 거북선 = new Line("bg-red-100", "거북선");
        Line 원양어선 = new Line("bg-black-200", "원양어선");
        lineDao.insert(거북선);
        lineDao.insert(원양어선);

        //when
        List<Line> lines = lineDao.findAll();

        //then
        assertThat(lines).hasSize(2);
        assertThat(lines).containsExactly(
                new Line(1L, "bg-red-100", "거북선"),
                new Line(2L, "bg-black-200", "원양어선")
        );
    }

    @Test
    @DisplayName("id를 통한 노선 조회")
    void findById() {
        //given
        Line 거북선 = lineDao.insert(new Line("bg-red-100", "거북선"));

        //when
        Line expectedLine = lineDao.findById(거북선.getId())
                .get();

        //then
        assertThat(expectedLine).isEqualTo(거북선);
    }

    @Test
    @DisplayName("노선 수정 테스트")
    void update() {
        //given
        Line 거북선 = lineDao.insert(new Line("bg-red-100", "거북선"));

        //when
        lineDao.update(거북선.getId(), "bg-yellow-100", "크로플선");

        //then
        Line expectedLine = lineDao.findById(거북선.getId())
                .get();
        assertThat(expectedLine.getColor()).isEqualTo("bg-yellow-100");
        assertThat(expectedLine.getName()).isEqualTo("크로플선");
    }

    @Test
    @DisplayName("노선 수정 예외 테스트")
    void update_duplicate() {
        //given
        Line 거북선 = lineDao.insert(new Line("bg-red-100", "거북선"));
        lineDao.insert(new Line("bg-yellow-100", "원양어선"));

        //when - then
        assertThatThrownBy(() -> lineDao.update(거북선.getId(), "bg-yellow-100", "원양어선"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("노선 삭제 테스트")
    void delete() {
        //given
        lineDao.insert(new Line("bg-red-100", "거북선"));
        Line 원양어선 = lineDao.insert(new Line("bg-yellow-100", "원양어선"));

        //when
        lineDao.delete(원양어선.getId());

        //then
        assertThat(lineDao.findAll()).hasSize(1);
    }
}