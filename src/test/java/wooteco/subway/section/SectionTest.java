package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("지하철 구간 도메인 테스트")
class SectionTest {

    @DisplayName("같은 upStationId를 가지는지 테스트")
    @Test
    void isSameUpStation() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        // when
        boolean sameUpStation = section.isSameUpStation(new Section(2L, 1L, 1L, 3L, 4));
        boolean differentUpStation = section.isSameUpStation(new Section(2L, 1L, 2L, 3L, 4));

        // then
        assertTrue(sameUpStation);
        assertFalse(differentUpStation);
    }

    @DisplayName("같은 downStationId를 가지는지 테스트")
    @Test
    void isSameDownStation() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        // when
        boolean sameDownStation = section.isSameUpStation(new Section(2L, 1L, 1L, 2L, 4));
        boolean differentDownStation = section.isSameUpStation(new Section(2L, 1L, 2L, 3L, 4));

        // then
        assertTrue(sameDownStation);
        assertFalse(differentDownStation);
    }

    @DisplayName("추가하려는 구간의 거리가 현재 구간의 거리보다 작은경우")
    @Test
    void updateDistance() {
        // given
        Section originalSection = new Section(1L, 1L, 1L, 2L, 3);

        // when
        originalSection.updateDistance(2);

        // then
        assertThat(originalSection.getDistance()).isEqualTo(1);
    }

    @DisplayName("추가하려는 구간의 거리가 현재 구간의 거리보다 같거나 큰 경우 예외 발생")
    @Test
    void updateDistanceLongerThanOriginal() {
        // given
        Section originalSection = new Section(1L, 1L, 1L, 2L, 3);

        // when & then
        assertThatThrownBy(() -> originalSection.updateDistance(4))
                .isInstanceOf(SubwayException.class);
        assertThatThrownBy(() -> originalSection.updateDistance(3))
                .isInstanceOf(SubwayException.class);
    }
}