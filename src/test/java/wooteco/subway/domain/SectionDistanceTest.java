package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.DistancePreviousOverException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[도메인] SectionDistance")
class SectionDistanceTest {

    @Test
    @DisplayName("빼기")
    void subtract() {
        SectionDistance 백 = new SectionDistance(100);
        SectionDistance 삼십 = new SectionDistance(30);
        SectionDistance 칠십 = new SectionDistance(70);

        assertThat(백.subtract(삼십)).isEqualTo(칠십);
    }

    @Test
    @DisplayName("정수변환")
    void intValue() {
        assertThat(new SectionDistance(3).intValue()).isEqualTo(3);
    }

    @Test
    @DisplayName("유효성 체크 - 자연수 검사")
    void validate() {
        assertThatThrownBy(() -> new SectionDistance(0))
                .isInstanceOf(DistancePreviousOverException.class);
    }
}