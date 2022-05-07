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
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 이름의 역은 저장할 수 없습니다.");
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
        final long id = 1L;
        given(stationDao.deleteById(id))
                .willReturn(1);

        // then
        assertThatCode(() -> stationService.delete(id))
                .doesNotThrowAnyException();
    }
}
