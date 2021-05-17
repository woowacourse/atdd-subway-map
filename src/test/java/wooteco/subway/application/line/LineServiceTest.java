package wooteco.subway.application.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.application.line.LineService;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.infrastructure.line.LineRepositoryImpl;
import wooteco.subway.infrastructure.station.StationRepositoryImpl;
import wooteco.util.LineFactory;
import wooteco.util.SectionFactory;
import wooteco.util.StationFactory;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepositoryImpl lineRepository;

    @Mock
    private StationRepositoryImpl stationRepository;

    @DisplayName("존재하지 않는 역 사이의 구간이 입력됐을 경우 예외")
    @Test
    void save_exceptionTestWhenNoExistingStationsIsInputted() {
        lenient().when(stationRepository.findById(1L))
                .thenReturn(StationFactory.create(1L, "a"));
        lenient().when(stationRepository.findById(2L))
                .thenThrow(new IllegalStateException("stationId가 존재하지 않습니다."));

        assertThatThrownBy(() -> lineService.save(
                LineFactory.create(1L, "a", "a", Collections.singletonList(
                        SectionFactory.create(1L, 1L, 2L, 10L)
                ))
        )).isInstanceOf(IllegalStateException.class).hasMessage("stationId가 존재하지 않습니다.");
    }

    @DisplayName("이미 존재하는 라인 이름을 입력받으면 예외")
    @Test
    void save_exceptionTestWhenGetANewLineRequestThatHasAlreadyExistingLineName() {
        given(lineRepository.contains(
                LineFactory.create("신분당선", "red", Collections.emptyList())
        )).willReturn(true);

        assertThatThrownBy(
                () -> lineService.save(
                        LineFactory.create("신분당선", "red", Collections.emptyList())
                ))
                .isInstanceOf(DuplicateLineException.class)
                .hasMessage("동알한 라인은 등록할 수 없습니다.");
    }

}