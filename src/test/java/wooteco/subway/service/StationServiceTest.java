package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class StationServiceTest extends ServiceTest {
    @InjectMocks
    private StationService stationService;

    @DisplayName("존재하지 않는 지하철 역을 반환하는 경우 예외를 발생한다.")
    @Test
    void save() {
        //given
        given(stationDao.findById(1L))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> {
            stationService.findById(1L);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 역입니다.");
    }
}
