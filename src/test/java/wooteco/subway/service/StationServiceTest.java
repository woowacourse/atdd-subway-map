package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.service.ServiceTestFixture.선릉역_요청;
import static wooteco.subway.service.ServiceTestFixture.일호선_생성;
import static wooteco.subway.service.ServiceTestFixture.잠실역_요청;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotFoundException;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("지하철 역 이름이 중복되지 않는다면 등록할 수 있다.")
    void save() {
        //when
        stationService.insert(선릉역_요청);

        //then
        assertThat(stationService.insert(잠실역_요청).getName()).isEqualTo("잠실");
    }

    @Test
    @DisplayName("지하철 역 이름이 중복된다면 등록할 수 없다.")
    void saveDuplicate() {
        //when
        stationService.insert(선릉역_요청);
        stationService.insert(잠실역_요청);

        //then
        assertThatThrownBy(() -> stationService.insert(잠실역_요청))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 이름이 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 역 목록을 조회할 수 있다.")
    void findAll() {
        //given
        stationService.insert(선릉역_요청);
        stationService.insert(잠실역_요청);

        //when
        List<String> responseNames = stationService.findAll().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        //then
        assertThat(responseNames).contains("선릉", "잠실");
    }

    @Test
    @DisplayName("존재하는 지하철 역을 삭제할 수 있다.")
    void delete() {
        //when
        StationResponse insert = stationService.insert(선릉역_요청);

        //then
        assertDoesNotThrow(() -> stationService.delete(insert.getId()));
    }

    @Test
    @DisplayName("존재하지 않는 지하철 역은 삭제할 수 없다.")
    void deleteNotFound() {
        //given
        stationService.insert(선릉역_요청);

        assertThatThrownBy(() -> stationService.delete(2L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }

    @Test
    @DisplayName("노선에 등록된 지하철 역은 삭제할 수 없다.")
    void deleteExistsInSection() {
        //given
        Long id1 = stationService.insert(선릉역_요청).getId();
        Long id2 = stationService.insert(잠실역_요청).getId();
        lineService.insert(일호선_생성(id1, id2));

        assertThatThrownBy(() -> stationService.delete(id1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 지하철역은 노선에 등록되어 있어 삭제할 수 없습니다.");
    }
}