package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static wooteco.subway.service.ServiceTestFixture.경의중앙_생성;
import static wooteco.subway.service.ServiceTestFixture.선릉역_요청;
import static wooteco.subway.service.ServiceTestFixture.수인분당선_수정;
import static wooteco.subway.service.ServiceTestFixture.이호선_생성;
import static wooteco.subway.service.ServiceTestFixture.일호선_생성;
import static wooteco.subway.service.ServiceTestFixture.일호선_수정;
import static wooteco.subway.service.ServiceTestFixture.잠실역_요청;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotFoundException;

@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    LineService lineService;

    @Autowired
    StationService stationService;

    private Long id1, id2;

    @BeforeEach
    void init() {
        id1 = stationService.insert(잠실역_요청).getId();
        id2 = stationService.insert(선릉역_요청).getId();
    }

    @Test
    @DisplayName("지하철 노선을 추가할 수 있다.")
    void insert() {
        //when
        LineResponse lineResponse = lineService.insert(일호선_생성(id1, id2));
        Line line = new Line(lineResponse.getId(), "1호선", "blue");

        Station station1 = new Station(id1, "잠실");
        Station station2 = new Station(id2, "선릉");

        List<StationResponse> stationsResponse = List.of(new StationResponse(station1), new StationResponse(station2));
        LineResponse expectedResponse = new LineResponse(line, stationsResponse);

        //then
        assertAll(
                () -> assertThat(expectedResponse.getId()).isEqualTo(lineResponse.getId()),
                () -> assertThat(expectedResponse.getName()).isEqualTo(lineResponse.getName()),
                () -> assertThat(expectedResponse.getColor()).isEqualTo(lineResponse.getColor()),
                () -> assertThat(expectedResponse.getStations()).containsAll(lineResponse.getStations())
        );
        assertThat(lineResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("지하철 노선 이름이 중복된다면 등록할 수 없다.")
    void insertErrorByDuplicateName() {
        //given
        lineService.insert(일호선_생성(id1, id2));

        //then
        assertThatThrownBy(() -> lineService.insert(일호선_생성(id1, id2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선 입력 시 id값이 동일하다면 등록할 수 없다.")
    void insertErrorByDuplicateStationId() {
        assertThatThrownBy(() -> lineService.insert(일호선_생성(id1, id1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행과 하행의 지하철 역이 같을 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선 입력 시 distance값이 0이하라면 등록할 수 없다.")
    void insertErrorByDistanceUnderZero() {
        assertThatThrownBy(() -> lineService.insert(new LineRequest.Post("1호선", "blue", 1L, 2L, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 양수여야 합니다.");
    }


    @Test
    @DisplayName("지하철 노선 목록을 조회할 수 있다.")
    void findAll() {
        //given
        lineService.insert(일호선_생성(id1, id2));
        lineService.insert(이호선_생성(id1, id2));

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
        LineResponse insert = lineService.insert(경의중앙_생성(id1, id2));

        //when & then
        assertDoesNotThrow(() -> lineService.update(insert.getId(), 수인분당선_수정));
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선을 수정할 수 없다.")
    void updateNotFound() {
        //given
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
        lineService.insert(일호선_생성(id1, id2));
        LineResponse insert = lineService.insert(경의중앙_생성(id1, id2));

        //when & then
        assertThatThrownBy(() -> lineService.update(insert.getId(), 일호선_수정))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선 이름이 중복될 수 없습니다.");
    }
}