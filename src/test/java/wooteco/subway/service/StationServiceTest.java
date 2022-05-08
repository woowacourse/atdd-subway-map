package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @DisplayName("save 메서드는 데이터를 저장한다.")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            StationRequest stationRequest = new StationRequest("태평역");
            given(stationDao.existByName("태평역"))
                .willReturn(false);
            given(stationDao.save(any(Station.class)))
                .willReturn(new Station(1L, stationRequest.getName()));

            StationResponse stationResponse = stationService.save(stationRequest);

            assertAll(() -> {
                assertThat(stationResponse.getId()).isEqualTo(1L);
                assertThat(stationResponse.getName()).isEqualTo("태평역");
            });
        }

        @Test
        void 중복되는_이름을_입력받은_경우_예외발생() {
            StationRequest stationRequest = new StationRequest("선릉역");
            given(stationDao.existByName("선릉역"))
                .willReturn(true);

            assertThatThrownBy(() -> stationService.save(stationRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void findAll_메서드는_모든_데이터를_조회한다() {
        List<Station> expected = List.of(new Station(1L, "태평역"), new Station(2L, "선릉역"));
        given(stationDao.findAll())
            .willReturn(expected);

        List<StationResponse> actual = stationService.findAll();

        assertAll(() -> {
            assertThat(actual.get(0).getId()).isEqualTo(expected.get(0).getId());
            assertThat(actual.get(0).getName()).isEqualTo(expected.get(0).getName());
            assertThat(actual.get(1).getId()).isEqualTo(expected.get(1).getId());
            assertThat(actual.get(1).getName()).isEqualTo(expected.get(1).getName());
        });
    }

    @DisplayName("delete 메서드는 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void 존재하는_역의_id가_엽력된_경우_성공() {
            given(stationDao.existById(1L))
                .willReturn(true);

            stationService.delete(1L);

            verify(stationDao).deleteById(1L);
        }

        @Test
        void 존재하지_않는_역의_id가_입력된_경우_예외발생() {
            given(stationDao.existById(1L))
                .willReturn(false);

            assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
