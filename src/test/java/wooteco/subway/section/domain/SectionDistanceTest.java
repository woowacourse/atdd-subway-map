package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.exception.SectionDistanceTooShortException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("구간 거리 기능 확인")
class SectionDistanceTest {
    @DisplayName("거리가 최소거리보다 작을 때 예외가 발생하는지 확인")
    @Test
    void whenDistanceIsNegative() {
        //given
        long negativeDistance = 0;
        //when
        //then
        assertThatThrownBy(() -> new SectionDistance(negativeDistance))
                .isInstanceOf(SectionDistanceTooShortException.class);
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

    @DisplayName("거리를 빼는 기능 확인")
    @Test
    void minus() {
        //given
        long aDistanceValue = 20;
        long anotherDistanceValue = 10;

        SectionDistance aSectionDistance = new SectionDistance(aDistanceValue);
        SectionDistance anotherSectionDistance = new SectionDistance(anotherDistanceValue);
        //when
        SectionDistance result = aSectionDistance.minus(anotherSectionDistance);

        //then
        assertThat(result.getDistance()).isEqualTo(aDistanceValue - anotherDistanceValue);
    }
}