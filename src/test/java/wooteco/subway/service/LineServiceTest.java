package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
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

}
