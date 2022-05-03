package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class StationTest {

    @ParameterizedTest(name = "이름 : {0}, 메시지 : {1}")
    @CsvSource({"12345678910, 지하철 역 이름은 10글자를 초과할 수 없습니다.", "1, 지하철 역 이름은 2글자 이상이어야 합니다."})
    @DisplayName("지하철 역 이름은 10글자 이하이다.")
    void inValidateName(String value, String message) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Station(value))
            .withMessage(message);
    }
}
