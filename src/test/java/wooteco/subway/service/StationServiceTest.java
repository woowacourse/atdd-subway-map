package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.dao.StationDao;

@SpringBootTest
class StationServiceTest {

    private StationService stationService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        StationDao stationDao = new StationDao(jdbcTemplate);
        stationService = new StationService(stationDao);
    }

    @Test
    @DisplayName("중복된 이름을 저장한다.")
    void duplicatedNameException() {
        //given
        String name = "선릉역";
        //when
        stationService.save(name);
        //then
        assertThatThrownBy(() -> stationService.save(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Station 이 존재합니다.");
    }


}