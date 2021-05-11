package wooteco.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import wooteco.subway.line.exception.LineIllegalArgumentException;
import wooteco.subway.section.domain.Section;

class LineRouteTest {

    @DisplayName("구간이 등록되지 않은 노선의 Route 를 생성할 수 없음")
    @ParameterizedTest
    @EmptySource
    void createLineRouteWhenSectionsIsEmpty(List<Section> sections) {
        assertThatThrownBy(() -> new LineRoute(sections))
            .isInstanceOf(LineIllegalArgumentException.class)
            .hasMessageContaining("구간이 등록되지 않은 정상적인");
    }

    @DisplayName("구간이 순서대로 정렬되는지 확인")
    @Test
    void sortSectionInLineRoute() {
        List<Section> sections = Arrays.asList(
            Section.of(1L, 1L, 3L, 3),
            Section.of(1L, 2L, 4L, 3),
            Section.of(1L, 3L, 2L, 3)
        );

        LineRoute lineRoute = new LineRoute(sections);

        assertThat(lineRoute.getStationIds()).containsExactly(1L, 3L, 2L, 4L);
    }

    @DisplayName("양쪽 끝에 구간을 삽입하는 경우를 확인하는지 테스트")
    @ParameterizedTest
    @CsvSource({"5, 1, true", "4, 5, true", "5, 4, false", "1, 5, false"})
    void isInsertSectionInEitherEndsOfLineTest(Long upStationId, Long downStationId,
        boolean isEitherEnsOfLine) {
        List<Section> sections = Arrays.asList(
            Section.of(1L, 1L, 3L, 3),
            Section.of(1L, 2L, 4L, 3),
            Section.of(1L, 3L, 2L, 3)
        );

        Section newSection = Section.of(1L, upStationId, downStationId, 1);

        LineRoute lineRoute = new LineRoute(sections);
        boolean insertSectionInEitherEndsOfLine = lineRoute
            .isInsertSectionInEitherEndsOfLine(newSection);

        assertThat(insertSectionInEitherEndsOfLine).isEqualTo(isEitherEnsOfLine);
    }

    @DisplayName("중간 삽입하는 경우를 업데이트가 필요한 section 정보를 반환하는지 테스트")
    @ParameterizedTest
    @CsvSource({"1, 4, 1, 4, 3, 2", "3, 4, 2, 4, 2, 1"})
    void isInsertSectionInEitherEndsOfLineTest(Long upStationId, Long downStationId, int distance,
        Long updatedUpStationId, Long updatedDownStationId, int updatedDistance) {
        List<Section> sections = Arrays.asList(
            Section.of(1L, 1L, 3L, 3),
            Section.of(1L, 2L, 4L, 3),
            Section.of(1L, 3L, 2L, 3)
        );

        Section newSection = Section.of(1L, upStationId, downStationId, distance);

        LineRoute lineRoute = new LineRoute(sections);
        Section sectionNeedToBeUpdatedForInsert = lineRoute
            .getSectionNeedToBeUpdatedForInsert(newSection);

        assertThat(sectionNeedToBeUpdatedForInsert.getUpStationId()).isEqualTo(updatedUpStationId);
        assertThat(sectionNeedToBeUpdatedForInsert.getDownStationId())
            .isEqualTo(updatedDownStationId);
        assertThat(sectionNeedToBeUpdatedForInsert.getDistance()).isEqualTo(updatedDistance);
    }
}
