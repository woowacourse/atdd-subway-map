package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("거리 관련 기능")
public class DistanceTest {

    @DisplayName("거리를 생성한다.")
    @Test
    void createDistance() {
        assertThatCode(() -> Distance.of(1)).doesNotThrowAnyException();
    }

    @DisplayName("잘못된 거리를 생성한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void createWrongName(int value) {
        assertThatIllegalArgumentException().isThrownBy(() -> Distance.of(value));
    }
}
