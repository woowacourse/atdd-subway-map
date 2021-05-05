package wooteco.subway.line;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.LineRequest;

class LineRequestTest {

    @Test
    @DisplayName("노선 관련 요청 생성")
    void createStationRequest() {
        // given

        // when
        LineRequest lineRequest = new LineRequest();

        // then
        assertThat(lineRequest).isInstanceOf(LineRequest.class);
    }

    @Test
    @DisplayName("노선 이름과 함께 노선 관련 요청 생성")
    void createStationRequestWithName() {
        // given
        String name = "3호선";
        String color = "초록색";

        // when
        LineRequest lineRequest = new LineRequest(name, color);

        // then
        assertThat(lineRequest).isInstanceOf(LineRequest.class);
        assertThat(lineRequest.getName()).isEqualTo(name);
        assertThat(lineRequest.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("노선 관련 요청에서 이름 가져오기")
    void getName() {
        // given
        LineRequest lineRequest1 = new LineRequest();

        String givenName2 = "정릉역";
        String givenColor2 = "빨간색";
        LineRequest lineRequest2 = new LineRequest(givenName2, givenColor2);

        // when
        String name1 = lineRequest1.getName();
        String name2 = lineRequest2.getName();
        String color1 = lineRequest1.getColor();
        String color2 = lineRequest2.getColor();

        // then
        assertThat(name1).isNull();
        assertThat(name2).isEqualTo(givenName2);
        assertThat(color1).isNull();
        assertThat(color2).isEqualTo(givenColor2);
    }

}