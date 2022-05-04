package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.domain.Line;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class LineDaoTest {

    private final LineDao lineDao;

    public LineDaoTest(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @BeforeEach
    void set() {
        lineDao.save("2호선", "green");
    }

    @AfterEach
    void reset() {
        lineDao.deleteAll();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        String expectedName = "1호선";
        String expectedColor = "blue";

        Line line = lineDao.save(expectedName, expectedColor);
        String actualName = line.getName();
        String actualColor = line.getColor();

        assertThat(actualName).isEqualTo(expectedName);
        assertThat(actualColor).isEqualTo(expectedColor);
    }

    @Test
    @DisplayName("중복된 노선을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        String name = "2호선";
        String color = "green";

        assertThatThrownBy(() -> lineDao.save(name, color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 노선입니다.");
    }
}
