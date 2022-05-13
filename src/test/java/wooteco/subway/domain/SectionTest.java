package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.domain.domainTestFixture.section1to2;
import static wooteco.subway.domain.domainTestFixture.section1to3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("상행과 하행의 id값이 같은 경우 에러를 발생시킨다")
    @Test
    void validateErrorBySameStationId() {
        assertThatThrownBy(() -> Section.of(1L, 1L, 1L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행과 하행의 지하철 역이 같을 수 없습니다.");
    }

    @DisplayName("거리가 0이하인 경우 에러를 발생시킨다.")
    @Test
    void validateErrorByNonPositiveDistance() {
        assertThatThrownBy(() -> Section.of(1L, 1L, 2L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 양수여야 합니다.");
    }

    @DisplayName("입력한 Section을 기준으로 기존 Section을 나눈다.")
    @Test
    void splitSection() {
        //given
        Section section1 = Section.of(1L, 2L, 10);
        Section section2 = Section.of(1L, 3L, 4);
        Section expectedSection = Section.of(3L, 2L, 6);

        //when
        Section splitSection = section1.splitSection(section2);

        //then
        assertAll(
                () -> assertThat(splitSection.getDistance()).isEqualTo(expectedSection.getDistance()),
                () -> assertThat(splitSection.isSameUpStationId(expectedSection.getUpStationId())).isTrue(),
                () -> assertThat(splitSection.isSameDownStationId(expectedSection.getDownStationId())).isTrue()
        );
    }

    @DisplayName("나누려는 Section의 distance가 기존 값보다 크거나 같은 경우 에러를 발생시킨다.")
    @Test
    void splitSectionErrorByLongerDistance() {
        assertThatThrownBy(() -> section1to2.splitSection(section1to3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 역 사이보다 긴 길이를 등록할 수 없습니다.");
    }

    @DisplayName("입력된 Section과 upStationId값이 같으면 true를 반환한다.")
    @Test
    void isSameUpStationId() {
        assertThat(section1to2.isSameUpStationId(1L)).isTrue();
    }

    @DisplayName("입력된 Section과 downStationId값이 같으면 true를 반환한다.")
    @Test
    void isSameDownStationIdById() {
        assertThat(section1to2.isSameDownStationId(2L)).isTrue();
    }
}