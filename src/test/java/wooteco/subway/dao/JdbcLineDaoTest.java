package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.LineEntity;

@JdbcTest
class JdbcLineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Line 을 저장한다.")
    void save() {
        //given
        LineEntity line = new LineEntity("7호선", "khaki");

        //when
        LineEntity actual = lineDao.save(line);

        //then
        checkHasSameNameAndColor(actual, line);
    }

    private void checkHasSameNameAndColor(LineEntity actual, LineEntity expected) {
        assertThat(actual).usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("전체 Line 목록을 조회한다.")
    void findAll() {
        //given
        LineEntity line1 = new LineEntity("7호선", "khaki");
        LineEntity line2 = new LineEntity("2호선", "green");
        lineDao.save(line1);
        lineDao.save(line2);

        //when
        List<LineEntity> actual = lineDao.findAll();

        //then
        checkHasSameNameAndColor(actual.get(0), line1);
        checkHasSameNameAndColor(actual.get(1), line2);
    }

    @Test
    @DisplayName("단일 Line 을 id 로 조회한다.")
    void findById() {
        //given
        LineEntity line = new LineEntity("7호선", "khaki");
        LineEntity savedLine = lineDao.save(line);

        //when
        LineEntity actual = lineDao.findById(savedLine.getId()).get();

        //then
        checkHasSameNameAndColor(actual, line);
    }

    @Test
    @DisplayName("이름으로 line 을 조회한다.")
    void findByName() {
        //given
        String name = "2호선";
        String color = "khaki";
        LineEntity savedLine = lineDao.save(new LineEntity(name, color));

        //when
        LineEntity actual = lineDao.findByName(name).get();

        //then
        checkHasSameNameAndColor(actual, savedLine);
    }

    @Test
    @DisplayName("Line 의 이름과 색깔을 변경한다.")
    void update() {
        //given
        LineEntity line = new LineEntity("7호선", "blue");
        LineEntity savedLine = lineDao.save(line);

        //when
        LineEntity updatedLine = new LineEntity(savedLine.getId(), "2호선", "khaki");
        lineDao.update(updatedLine);

        //then
        LineEntity actual = lineDao.findById(savedLine.getId()).get();
        checkHasSameNameAndColor(actual, updatedLine);
    }

    @Test
    @DisplayName("Line 을 삭제한다.")
    void deleteById() {
        //given
        LineEntity line = new LineEntity("7호선", "khaki");
        LineEntity savedLine = lineDao.save(line);

        //when
        lineDao.deleteById(savedLine.getId());

        //then
        assertThat(lineDao.findById(savedLine.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 id 를 삭제하면 0을 반환한다.")
    void deleteByIdWithIdNotExists() {
        //when
        int actual = lineDao.deleteById(1L);

        //then
        assertThat(actual).isEqualTo(0);
    }
}
