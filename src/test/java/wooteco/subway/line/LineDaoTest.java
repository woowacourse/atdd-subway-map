package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.LineNotExistException;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixture.makeLine;
import static wooteco.subway.Fixture.makeStation;

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
        // given
        Long id = lineDao.insert(makeLine("bg-red-100", "코다선"));
        final Line line = lineDao.findById(id).orElseThrow(LineNotExistException::new);

        assertThat(line.getColor()).isEqualTo("bg-red-100");
        assertThat(line.getName()).isEqualTo("코다선");
    }

    @Test
    @DisplayName("노선 목록 조회")
    void findAll() {
        //given
        final Long 거북선_아이디 = lineDao.insert(makeLine("bg-red-100", "거북선"));
        final Long 원양어선_아이디 = lineDao.insert(makeLine("bg-black-200", "원양어선"));

        //when
        List<Long> lineIds = lineDao.findAll().stream()
                .map(Line::getId)
                .collect(Collectors.toList());

        //then
        assertThat(lineIds).containsExactly(거북선_아이디, 원양어선_아이디);
    }

    @Test
    @DisplayName("id를 통한 노선 조회")
    void findById() {
        //given
        Long id = lineDao.insert((makeLine("bg-red-100", "거북선")));

        //when
        Line expectedLine = lineDao.findById(id).orElseThrow(LineNotExistException::new);

        //then
        assertThat(expectedLine.getName()).isEqualTo("거북선");
        assertThat(expectedLine.getColor()).isEqualTo("bg-red-100");
    }

    @Test
    @DisplayName("노선 수정 테스트")
    void update() {
        //given
        Long id = lineDao.insert(makeLine("bg-red-100", "거북선"));

        //when
        lineDao.update(id, makeLine("bg-yellow-100", "크로플선"));

        //then
        Line expectedLine = lineDao.findById(id).orElseThrow(LineNotExistException::new);

        assertThat(expectedLine.getColor()).isEqualTo("bg-yellow-100");
        assertThat(expectedLine.getName()).isEqualTo("크로플선");
    }

    @Test
    @DisplayName("노선 수정 예외 테스트")
    void update_duplicate() {
        //given
        Long id = lineDao.insert(makeLine("bg-red-100", "거북선"));
        lineDao.insert(makeLine("bg-yellow-100", "원양어선"));

        //when - then
        assertThatThrownBy(() -> lineDao.update(id, makeLine("bg-yellow-100", "원양어선")))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("노선 삭제 테스트")
    void delete() {
        //given
        Long expected_id = lineDao.insert(makeLine("bg-red-100", "거북선"));
        Long id = lineDao.insert(makeLine("bg-yellow-100", "원양어선"));

        //when
        lineDao.delete(id);

        //then
        assertThat(lineDao.findAll().stream()
                .map(Line::getId)
                .collect(Collectors.toList()))
                .containsExactly(expected_id);
    }

    @Test
    @DisplayName("id로 노선명 조회")
    void findNameById() {
        //given
        final Long id = lineDao.insert(makeLine("bg-red-100", "여객선"));

        //when
        final String name = lineDao.findNameById(id);

        //then
        assertThat(name).isEqualTo("여객선");
    }

    @Test
    @DisplayName("id로 색 조회")
    void findColorById() {
        //given
        final Long id = lineDao.insert(makeLine("bg-red-100", "여객선"));

        //when
        final String color = lineDao.findColorById(id);

        //then
        assertThat(color).isEqualTo("bg-red-100");
    }

    @Test
    @DisplayName("같은 이름을 가진 노선이 존재하지 않을 때 개수 조회")
    void countsByName1() {
        //given - when
        final int cnt = lineDao.countsByName("테스트노선");

        //then
        assertThat(cnt).isEqualTo(0);
    }

    @Test
    @DisplayName("같은 이름을 가진 역이 존재할 때 개수 조회")
    void countsByName2() {
        //given
        final Long id = lineDao.insert(makeLine("bg-red-100","잠실역"));
        final String name = lineDao.findNameById(id);

        //when
        final int cnt = lineDao.countsByName(name);

        //then
        assertThat(cnt).isEqualTo(1);
    }

    @Test
    @DisplayName("같은 색을 가진 노선이 존재하지 않을 때 개수 조회")
    void countsByColor1() {
        //given - when
        final int cnt = lineDao.countsByColor("bg-red-100");

        //then
        assertThat(cnt).isEqualTo(0);
    }

    @Test
    @DisplayName("같은 이름을 가진 역이 존재할 때 개수 조회")
    void countsByColor2() {
        //given
        final Long id = lineDao.insert(makeLine("bg-red-100","잠실역"));
        final String color = lineDao.findColorById(id);

        //when
        final int cnt = lineDao.countsByColor(color);

        //then
        assertThat(cnt).isEqualTo(1);
    }
}