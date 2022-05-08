package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class StationTest {

    @ParameterizedTest(name = "이름 : {0}, 메시지 : {1}")
    @CsvSource({"12345678910, 지하철 역 이름은 10글자를 초과할 수 없습니다.", "1, 지하철 역 이름은 2글자 이상이어야 합니다."})
    @DisplayName("지하철 역 이름이 2글자 미만 10글자 초과일 경우 예외를 발생한다.")
    void inValidName(String value, String message) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Station(value))
            .withMessage(message);
    }

    @ParameterizedTest
    @ValueSource(strings = {"망one", "wkatlf", "hoho역", "sudal1"})
    @DisplayName("지하철 역이름은 한글과 숫자가 아닌 경우 예외를 발생한다.")
    void invalidName(String name) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Station(name))
            .withMessage("지하철 역 이름은 한글과 숫자이어야 합니다.");
    }

}
