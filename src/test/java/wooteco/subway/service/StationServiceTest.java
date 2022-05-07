package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @DisplayName("새로운 지하철역을 등록한다.")
    @Test
    void createStation() {
        final String name = "선릉역";
        final Station station = new Station(name);
        given(stationDao.save(station)).willReturn(new Station(1L, name));

        final Station actual = stationService.createStation(station);

        assertAll(
                () -> assertThat(actual.getId()).isOne(),
                () -> assertThat(actual.getName()).isEqualTo(name)
        );
    }

    @DisplayName("중복된 이름의 지하철역을 등록할 경우 예외를 발생한다.")
    @Test
    void createStation_throwsExceptionWithDuplicateName() {
        final String name = "선릉역";
        final Station station = new Station(name);
        given(stationDao.existByName("선릉역")).willReturn(true);

        assertThatThrownBy(() -> stationService.createStation(station))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessage("이미 존재하는 지하철 역입니다.");
    }

    @DisplayName("등록된 모든 지하철역을 반환한다.")
    @Test
    void getAllStations() {
        final Station station1 = new Station("강남역");
        final Station station2 = new Station("역삼역");
        final Station station3 = new Station("선릉역");
        final List<Station> expected = List.of(station1, station2, station3);
        given(stationDao.findAll()).willReturn(expected);

        final List<Station> actual = stationService.getAllStations();

        assertThat(actual).containsAll(expected);
    }

    @DisplayName("등록된 지하철역을 삭제한다.")
    @Test
    void delete() {
        final long id = 1L;
        final String name = "선릉역";
        final Station station = new Station(id, name);

        given(stationDao.existById(1L)).willReturn(true);

        stationService.delete(1L);
        verify(stationDao, times(1)).deleteById(1L);
    }

    @DisplayName("삭제하려는 지하철 역 ID가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void delete_throwsExceptionIfIdNotExist() {
        final long id = 1L;
        final String name = "선릉역";
        final Station station = new Station(id, name);

        given(stationDao.existById(1L)).willReturn(false);
        stationService.createStation(station);

        assertThatThrownBy(() -> stationService.delete(id))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("삭제하려는 지하철 역 ID가 존재하지 않습니다.");
    }
}
