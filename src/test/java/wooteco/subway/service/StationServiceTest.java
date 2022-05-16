package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationRequest;
import wooteco.subway.dto.station.StationResponse;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.station.DuplicateStationException;

class StationServiceTest extends ServiceTest {

    @InjectMocks
    private StationService stationService;

    @Test
    @DisplayName("역을 생성한다.")
    void Create() {
        // given
        final String name = "강남역";

        final StationRequest request = new StationRequest(name);
        final Station expected = new Station(name);

        given(stationDao.insert(expected))
                .willReturn(Optional.of(expected));

        // when
        final StationResponse actual = stationService.create(request);

        // then
        assertThat(actual.getName()).isEqualTo(expected.getName());
    }

    @Test
    @DisplayName("저장하려는 역 이름이 중복되면 예외를 던진다.")
    void Create_DuplicateName_ExceptionThrown() {
        // given
        final String name = "강남역";

        final StationRequest request = new StationRequest(name);

        given(stationDao.insert(any(Station.class)))
                .willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> stationService.create(request))
                .isInstanceOf(DuplicateStationException.class);
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void FindAll() {
        // given
        final Station station1 = new Station("노원역");
        final Station station2 = new Station("왕십리역");
        final List<Station> expected = List.of(station1, station2);

        given(stationDao.findAll())
                .willReturn(expected);

        // when
        final List<StationResponse> actual = stationService.findAll();

        // then
        assertThat(actual).hasSameSizeAs(expected);
    }

    @Test
    @DisplayName("id에 해당하는 역을 삭제한다.")
    void Delete() {
        // given
        given(sectionDao.existStation(any(Long.class)))
                .willReturn(false);
        given(stationDao.deleteById(any(Long.class)))
                .willReturn(1);

        // then
        assertThatCode(() -> stationService.delete(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("id에 해당하는 역이 구간에 등록되어 있으면 삭제할 수 없다.")
    void Delete_RegisteredInSection_ExceptionThrown() {
        // given
        given(sectionDao.existStation(any(Long.class)))
                .willReturn(true);

        // then
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("역이 구간에 등록되어 있습니다.");
    }
}
