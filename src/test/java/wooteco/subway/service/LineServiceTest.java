package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.dao.LineDao;

@SpringBootTest
class LineServiceTest {

    private LineService lineService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        LineDao lineDao = new LineDao(jdbcTemplate);
        lineService = new LineService(lineDao);
    }

    @Test
    @DisplayName("중복된 이름을 저장한다.")
    void duplicatedNameException() {
        //given
        String name = "선릉역";
        String color = "red";
        String color2 = "blue";
        //when
        lineService.save(name, color);
        //then
        assertThatThrownBy(() -> lineService.save(name, color2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @Test
    @DisplayName("중복된 색을 저장한다.")
    void duplicatedColorException() {
        //given
        String name = "강남역";
        String name2 = "교대역";
        String color = "orange";
        //when
        lineService.save(name, color);
        //then
        assertThatThrownBy(() -> lineService.save(name2, color))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 Id 를 조회한다.")
    void findByIdException() {
        //given
        Long id = -1L;
        //when

        //then
        assertThatThrownBy(() -> lineService.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID의 노선은 존재하지 않습니다.");
    }

}