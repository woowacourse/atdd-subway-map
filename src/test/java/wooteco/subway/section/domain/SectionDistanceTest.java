package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("구간 거리 기능 확인")
class SectionDistanceTest {
    @DisplayName("거리가 음수일 때 예외가 발생하는지 확인")
    @Test
    void whenDistanceIsNegative() {
        //given
        long negativeDistance = -1;
        //when
        //then
        assertThatThrownBy(() -> new SectionDistance(negativeDistance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간 거리는 음수일 수 없습니다. 입력된 거리");
    }

    @DisplayName("거리를 합하는 기능 확인")
    @Test
    void sum() {
        //given
        long aDistanceValue = 10;
        long anotherDistanceValue = 20;

        SectionDistance aSectionDistance = new SectionDistance(aDistanceValue);
        SectionDistance anotherSectionDistance = new SectionDistance(anotherDistanceValue);

        //when
        SectionDistance result = aSectionDistance.sum(anotherSectionDistance);
        //then
        assertThat(result.getDistance()).isEqualTo(aDistanceValue + anotherDistanceValue);
    }
}