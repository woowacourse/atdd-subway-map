package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineBasicRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql({"classpath:schema-test.sql", "classpath:service-test-ddl.sql"})
class LineServiceTest {

    private static final LineResponse LINE_RESPONSE1 = new LineResponse(
        1L, "분당선", "노란색", List.of(
        new StationResponse(1L, "복정역"),
        new StationResponse(2L, "가천대역"),
        new StationResponse(3L, "태평역")
    ));
    private static final LineResponse LINE_RESPONSE2 = new LineResponse(
        2L, "2호선", "초록색", List.of(
        new StationResponse(4L, "잠실역"),
        new StationResponse(5L, "잠실나루역"),
        new StationResponse(6L, "강변역")
    ));

    @Autowired
    private LineService lineService;


    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            LineRequest lineRequest = new LineRequest("5호선", "보라색", 1L, 2L, 10);

            LineResponse actual = lineService.save(lineRequest);
            LineResponse expected = new LineResponse(3L, "5호선", "보라색", List.of(
                new StationResponse(1L, "복정역"),
                new StationResponse(2L, "가천대역")
            ));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름인_경우_예외발생() {
            LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 10);

            assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_상행역을_입력한_경우_예외발생() {
            LineRequest lineRequest = new LineRequest("3호선", "주황색", 9L, 2L, 10);

            assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_하행역_입력한_경우_예외발생() {
            LineRequest lineRequest = new LineRequest("3호선", "주황색", 2L, 9L, 10);

            assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void findAll_메서드는_모든_데이터를_조회한다() {
        List<LineResponse> actual = lineService.findAll();

        List<LineResponse> expected = List.of(
            LINE_RESPONSE1, LINE_RESPONSE2
        );

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("findById 메서드는 단건의 데이터를 조회한다.")
    @Nested
    class FindByTest {

        @Test
        void 존재하는_노선의_id가_입력된_경우_성공() {
            assertThat(lineService.findById(1L)).isEqualTo(LINE_RESPONSE1);
        }

        @Test
        void 존재하지_않는_역의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> lineService.findById(9999L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("update 메서드는 데이터를 수정한다.")
    @Nested
    class UpdateClass {

        @Test
        void 유효한_입력값인_경우_성공() {
            LineBasicRequest lineRequest = new LineBasicRequest("5호선", "보라색");

            lineService.update(1L, lineRequest);

            LineResponse actual = lineService.findById(1L);
            assertAll(() -> {
                assertThat(actual.getName()).isEqualTo("5호선");
                assertThat(actual.getColor()).isEqualTo("보라색");
            });
        }

        @Test
        void 중복되는_이름으로_수정하려는_경우_예외발생() {
            LineBasicRequest lineRequest = new LineBasicRequest("2호선", "보라색");

            assertThatThrownBy(() -> lineService.update(1L, lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_노선을_수정하려는_경우_예외발생() {
            LineBasicRequest lineRequest = new LineBasicRequest("1호선", "보라색");

            assertThatThrownBy(() -> lineService.update(9999L, lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("delete 메서드는 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void 존재하는_노선의_id가_입력된_경우_성공() {
            assertThatCode(() -> lineService.delete(1L)).doesNotThrowAnyException();
        }

        @Test
        void 존재하지_않는_노선의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> lineService.delete(9999L))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("addStationToLine 메서드는 노선에 구간을 추가한다.")
    @Nested
    class AddStationToLineTest {

        @Test
        void 유효한_구간을_입력한_경우_성공() {
            SectionRequest request = new SectionRequest(2L, 8L, 3);

            assertThatCode(() -> lineService.addStationToLine(1L, request)).doesNotThrowAnyException();
        }

        @Test
        void 상행역과_하행역이_둘다_등록되어_있는_경우_예외발생() {
            SectionRequest request = new SectionRequest(1L, 3L, 3);

            assertThatThrownBy(() -> lineService.addStationToLine(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 상행역과_하행역이_둘다_포함되어_있지_않은_경우_예외발생() {
            SectionRequest request = new SectionRequest(7L, 8L, 3);

            assertThatThrownBy(() -> lineService.addStationToLine(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 기존_역_사이_길이보다_크면_예외발생() {
            SectionRequest request = new SectionRequest(1L, 8L, 12);

            assertThatThrownBy(() -> lineService.addStationToLine(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 기존_역_사이_길이와_같으면_예외발생() {
            SectionRequest request = new SectionRequest(1L, 8L, 10);

            assertThatThrownBy(() -> lineService.addStationToLine(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("removeStationToLine 메서드는 노선에 구간을 삭제한다.")
    @Nested
    class RemoveStationToLine {

        @Test
        void 존재하지_않는_노선_id로_조회하는_경우_예외발생() {
            assertThatThrownBy(() -> lineService.removeStationToLine(5L, 3L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void 유효한_값을_입력한_경우_삭제_성공() {
            assertThatCode(() -> lineService.removeStationToLine(1L, 2L))
                .doesNotThrowAnyException();
        }
    }
}
