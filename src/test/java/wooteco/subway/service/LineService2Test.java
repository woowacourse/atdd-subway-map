package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
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

    private static final StationResponse STATION_RESPONSE_1 = new StationResponse(1L, "이미 존재하는 역 이름");
    private static final StationResponse STATION_RESPONSE_2 = new StationResponse(2L, "선릉역");
    private static final StationResponse STATION_RESPONSE_3 = new StationResponse(3L, "잠실역");

    @Autowired
    private LineService2 service;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("find 메서드는 특정 id에 해당되는 데이터를 조회한다")
    @Nested
    class FindTest {

        @Test
        void 구간_정보를_포함한_노선의_모든_정보_조회() {
            LineResponse2 actual = service.find(2L);

            LineResponse2 expected = new LineResponse2(2L, "신분당선", "빨간색",
                    List.of(STATION_RESPONSE_1, STATION_RESPONSE_2, STATION_RESPONSE_3));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 존재하지_않는_노선인_경우_예외_발생() {
            assertThatThrownBy(() -> service.find(99999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 유효한_입력인_경우_성공() {
            LineResponse2 actual = service.save(new CreateLineRequest(
                    VALID_LINE_NAME, COLOR, 1L, 2L, DISTANCE));

            LineResponse2 expected = new LineResponse2(4L, VALID_LINE_NAME, COLOR,
                    List.of(STATION_RESPONSE_1, STATION_RESPONSE_2));

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

    @DisplayName("delete 메서드는 노선과 모든 구간 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void 존재하는_데이터의_id가_입력된_경우_삭제성공() {
            service.delete(1L);

            boolean lineNotFound = lineDao.findById(1L).isEmpty();
            List<?> sectionsConnectedToLine = sectionDao.findAllByLineId(1L);

            assertThat(lineNotFound).isTrue();
            assertThat(sectionsConnectedToLine).isEmpty();
        }

        @Test
        void 존재하지_않는_데이터의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> service.delete(99999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
