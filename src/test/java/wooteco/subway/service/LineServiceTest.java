package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.service.ServiceTestFixture.경의중앙_생성;
import static wooteco.subway.service.ServiceTestFixture.동두천역_요청;
import static wooteco.subway.service.ServiceTestFixture.선릉역_요청;
import static wooteco.subway.service.ServiceTestFixture.수인분당선_수정;
import static wooteco.subway.service.ServiceTestFixture.이호선_생성;
import static wooteco.subway.service.ServiceTestFixture.이호선_수정;
import static wooteco.subway.service.ServiceTestFixture.일호선_생성;
import static wooteco.subway.service.ServiceTestFixture.잠실역_요청;
import static wooteco.subway.service.ServiceTestFixture.지행역_요청;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.NotFoundException;

@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    LineService lineService;

    @Autowired
    StationService stationService;

    @BeforeEach
    void init() {
        Long id1 = stationService.insert(잠실역_요청).getId();
        Long id2 = stationService.insert(동두천역_요청).getId();

        lineService.insert(일호선_생성(id1, id2));
        lineService.insert(이호선_생성(id1, id2));
    }

    @Test
    @DisplayName("지하철 노선 이름이 중복된다면 등록할 수 없다.")
    void saveDuplicate() {

        Long id1 = stationService.insert(지행역_요청).getId();
        Long id2 = stationService.insert(선릉역_요청).getId();

        assertThatThrownBy(() -> lineService.insert(일호선_생성(id1, id2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회할 수 있다.")
    void findAll() {
        //when
        List<LineResponse> lineResponses = lineService.findAll();

        List<String> names = lineResponses.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());

        List<String> colors = lineResponses.stream()
                .map(LineResponse::getColor)
                .collect(Collectors.toList());

        //then
        assertAll(
                () -> assertThat(names).containsOnly("1호선", "2호선"),
                () -> assertThat(colors).containsOnly("green", "blue")
        );
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선은 조회할 수 없다.")
    void findByIdNotFound() {
        assertThatThrownBy(() -> lineService.findById(10L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선은 삭제할 수 없다.")
    void deleteByIdNotFound() {
        assertThatThrownBy(() -> lineService.deleteById(30L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("존재하는 지하철 노선을 수정할 수 있다.")
    void update() {
        //given
        Long id1 = stationService.insert(지행역_요청).getId();
        Long id2 = stationService.insert(선릉역_요청).getId();

        LineResponse insert = lineService.insert(경의중앙_생성(id1, id2));

        //when & then
        assertDoesNotThrow(() -> lineService.update(insert.getId(), 수인분당선_수정));
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선을 수정할 수 없다.")
    void updateNotFound() {
        //given
        Long id1 = stationService.insert(지행역_요청).getId();
        Long id2 = stationService.insert(선릉역_요청).getId();

        LineResponse insert = lineService.insert(경의중앙_생성(id1, id2));

        //when & then
        assertThatThrownBy(() -> lineService.update(insert.getId() + 1, 수인분당선_수정))
                .isInstanceOf(NotFoundException.class)
               .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("지하철 노선 이름이 중복된다면 수정할 수 없다.")
    void updateDuplicate() {
        //given
        Long id1 = stationService.insert(지행역_요청).getId();
        Long id2 = stationService.insert(선릉역_요청).getId();

        LineResponse insert = lineService.insert(경의중앙_생성(id1, id2));

        //when & then
        assertThatThrownBy(() -> lineService.update(insert.getId(), 이호선_수정))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복될 수 없습니다.");
    }
}