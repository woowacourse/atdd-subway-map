package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.response.LineResponse2;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class LineService2Test extends ServiceTest {

    private static final String VALID_LINE_NAME = "새로운 노선";
    private static final String COLOR = "노란색";
    private static final long VALID_UP_STATION_ID = 1L;
    private static final long VALID_DOWN_STATION_ID = 2L;
    private static final int DISTANCE = 10;
    private static final long INVALID_ID = 999999L;

    @Autowired
    private LineService2 service;

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 유효한_입력인_경우_성공() {
            LineResponse2 actual = service.save(new CreateLineRequest(
                    VALID_LINE_NAME, COLOR, VALID_UP_STATION_ID, VALID_DOWN_STATION_ID, DISTANCE));

            LineResponse2 expected = new LineResponse2(4L, VALID_LINE_NAME, COLOR,
                    List.of(new StationResponse(VALID_UP_STATION_ID, "이미 존재하는 역 이름"),
                            new StationResponse(VALID_DOWN_STATION_ID, "선릉역")));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_노선명인_경우_예외발생() {
            CreateLineRequest duplicateLineNameRequest = new CreateLineRequest(
                    "이미 존재하는 노선 이름", COLOR, VALID_UP_STATION_ID, VALID_DOWN_STATION_ID, DISTANCE);
            assertThatThrownBy(() -> service.save(duplicateLineNameRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_상행역을_입력한_경우_예외발생() {
            CreateLineRequest noneExistingUpStationRequest = new CreateLineRequest(
                    VALID_LINE_NAME, COLOR, INVALID_ID, VALID_DOWN_STATION_ID, DISTANCE);
            assertThatThrownBy(() -> service.save(noneExistingUpStationRequest))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        void 존재하지_않는_하행역을_입력한_경우_예외발생() {
            CreateLineRequest noneExistingUpStationRequest = new CreateLineRequest(
                    VALID_LINE_NAME, COLOR, VALID_UP_STATION_ID, INVALID_ID, DISTANCE);
            assertThatThrownBy(() -> service.save(noneExistingUpStationRequest))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
