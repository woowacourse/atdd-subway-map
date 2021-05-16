package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DuplicateException;

class SectionTest {

    private final Line 칠호선 = new Line(7L, "7호선", "bg-green-100");
    private final Station 상봉역 = new Station(1L, "상봉역");
    private final Station 면목역 = new Station(2L, "면목역");
    private final Station 사가정역 = new Station(3L, "사가정역");
    private final Station 용마산역 = new Station(4L, "용마산역");
    private final Distance 거리10 = new Distance(10);
    private final Distance 거리20 = new Distance(20);
    private final Distance 거리5 = new Distance(5);

    @Test
    @DisplayName("중복된 지하철역이 입력되었을 시 예외처리")
    public void validateDuplicatedStation() {
        // given

        // when

        // then
        assertThatThrownBy(() -> new Section(칠호선, 상봉역, 상봉역, 거리10))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("구간 등록을 위해 기존 연결 지하철 역과 거리가 달라진 새로운 구간 생성")
    void updateForSave() {
        // given
        Section section = new Section(칠호선, 상봉역, 면목역, 거리10);
        Section newSection = new Section(칠호선, 상봉역, 사가정역, 거리5);

        // when
        Section updatedSection = section.updateForSave(newSection);

        // then
        assertThat(updatedSection.getUpStation()).isEqualTo(사가정역);
        assertThat(updatedSection.getDownStation()).isEqualTo(면목역);
        assertThat(updatedSection.getDistanceValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("구간 삭제를 위해 기존 연결 지하철 역과 거리가 달라진 새로운 구간 생성")
    void updateForDelete() {
        // given
        Section section = new Section(칠호선, 상봉역, 면목역, 거리10);
        Section newSection = new Section(칠호선, 면목역, 사가정역, 거리5);

        // when
        Section updatedSection = section.updateForDelete(newSection);

        // then
        assertThat(updatedSection.getUpStation()).isEqualTo(상봉역);
        assertThat(updatedSection.getDownStation()).isEqualTo(사가정역);
        assertThat(updatedSection.getDistanceValue()).isEqualTo(15);
    }

    @Test
    @DisplayName("구간과 비교했을 때 서로 일치하는 지하철 상행/하행역을 가졌는지 확인")
    void hasSameStation() {
        // given
        Section section = new Section(칠호선, 상봉역, 면목역, 거리10);
        Section compareSection1 = new Section(칠호선, 용마산역, 면목역, 거리20);
        Section compareSection2 = new Section(칠호선, 용마산역, 사가정역, 거리20);

        // when
        boolean hasSameStation = section.hasSameStationBySection(compareSection1);
        boolean noHasSameStation = section.hasSameStationBySection(compareSection2);

        // then
        assertThat(hasSameStation).isTrue();
        assertThat(noHasSameStation).isFalse();
    }
}