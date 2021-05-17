package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wooteco.subway.line.LineFixture.신분당선;
import static wooteco.subway.line.LineFixture.이호선;
import static wooteco.subway.station.StationFixture.*;

@DisplayName("지하철 구간 도메인 테스트")
class SectionTest {

    @DisplayName("같은 upStation을 가지는지 테스트")
    @Test
    void isSameUpStation() {
        // given
        Section section = new Section(1L, 이호선, 왕십리역, 잠실역, 10);

        // when
        boolean sameUpStation = section.isSameUpStation(왕십리역);
        boolean differentUpStation = section.isSameUpStation(강남역);

        // then
        assertTrue(sameUpStation);
        assertFalse(differentUpStation);
    }

    @DisplayName("같은 downStation을 가지는지 테스트")
    @Test
    void isSameDownStation() {
        // given
        Section section = new Section(1L, 이호선, 왕십리역, 잠실역, 10);

        // when
        boolean sameDownStation = section.isSameDownStation(잠실역);
        boolean differentDownStation = section.isSameDownStation(강남역);

        // then
        assertTrue(sameDownStation);
        assertFalse(differentDownStation);
    }

    @DisplayName("추가하려는 구간의 거리가 현재 구간의 거리보다 작은경우")
    @Test
    void updateDistance() {
        // given
        Section originalSection = new Section(1L, 이호선, 왕십리역, 잠실역, 10);

        // when
        originalSection.updateDistance(2);

        // then
        assertThat(originalSection.getDistance()).isEqualTo(8);
    }

    @DisplayName("추가하려는 구간의 거리가 현재 구간의 거리보다 같거나 큰 경우 예외 발생")
    @Test
    void updateDistanceLongerThanOriginal() {
        // given
        Section originalSection = new Section(1L, 이호선, 왕십리역, 잠실역, 10);

        // when & then
        assertThatThrownBy(() -> originalSection.updateDistance(10))
                .isInstanceOf(SubwayException.class);
        assertThatThrownBy(() -> originalSection.updateDistance(11))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간의 upStation 수정")
    @Test
    void updateUpStation() {
        // given
        Section originalSection = new Section(1L, 이호선, 왕십리역, 잠실역, 10);

        // when
        originalSection.updateUpStation(강남역);

        // then
        assertThat(originalSection.getUpStation()).isEqualTo(강남역);
    }

    @DisplayName("구간의 downStation 수정")
    @Test
    void updateDownStation() {
        // given
        Section originalSection = new Section(1L, 이호선, 왕십리역, 잠실역, 10);

        // when
        originalSection.updateDownStation(강남역);

        // then
        assertThat(originalSection.getDownStation()).isEqualTo(강남역);
    }

    @DisplayName("구간의 line, upStaion, downStation 수정")
    @Test
    void update() {
        // given
        Section originalSection = new Section(1L, 이호선, 왕십리역, 잠실역, 10);

        // when
        originalSection.update(신분당선, 강남역, 구의역);

        // then
        assertThat(originalSection.getLine()).isEqualTo(신분당선);
        assertThat(originalSection.getUpStation()).isEqualTo(강남역);
        assertThat(originalSection.getDownStation()).isEqualTo(구의역);
    }
}