package wooteco.subway.domain.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionTest {

    private final Station longerUpStation = new Station("천호역");
    private final Station longerDownStation = new Station("강남역");

    @DisplayName("상행과 하행이 같은 구간은 생성할 수 없다.")
    @Test
    void cannotMake() {
        assertThatCode(() -> new Section(longerUpStation, longerUpStation, 10, 1L))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionStatus.INVALID_SECTION.getMessage());
    }

    @DisplayName("두 구간을 더한다.")
    @Test
    void append() {
        Station newDownStation = new Station("의정부역");
        Section firstSection = new Section(longerUpStation, longerDownStation, 10, 1L);
        Section secondSection = new Section(longerDownStation, newDownStation, 5, 1L);

        Section appendedSection = firstSection.append(secondSection);

        assertThat(appendedSection).isEqualTo(new Section(longerUpStation, newDownStation, 15, 1L));
    }

    @DisplayName("연결되지 않은 노선은 더할 수 없다.")
    @Test
    void cannotAppend() {
        Station newDownStation = new Station("의정부역");
        Section firstSection = new Section(longerUpStation, longerDownStation, 10, 1L);
        Section secondSection = new Section(longerUpStation, newDownStation, 5, 1L);

        assertThatCode(() -> firstSection.append(secondSection))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionStatus.SECTION_NOT_CONNECTABLE.getMessage());
    }

    @DisplayName("두 구간이 연결되어 있다면 ture를 반환한다.")
    @Test
    void isConnectedTowardWith() {
        Station newDownStation = new Station("의정부역");
        Section firstSection = new Section(longerUpStation, longerDownStation, 10, 1L);
        Section secondSection = new Section(longerDownStation, newDownStation, 5, 1L);

        assertThat(firstSection.isConnectedTowardDownWith(secondSection)).isTrue();
        assertThat(secondSection.isConnectedTowardDownWith(firstSection)).isFalse();
    }

    @DisplayName("splitLongerSectionBy 메서드는")
    @Nested
    class Describe_splitLongerSectionBy {

        @DisplayName("짧은 구간의 길이가 긴 구간의 길이 이상일 때")
        @Nested
        class Context_shorterDistanceAreGreaterThanLongerDistance {

            @DisplayName("예외가 발생한다.")
            @Test
            void cannotSplit() {
                Station shorterDownStation = new Station("잠실역");
                Section longerSection = new Section(longerUpStation, longerDownStation, 5, 1L);
                Section shorterSection = new Section(longerUpStation, shorterDownStation, 15, 1L);

                assertThatCode(() -> longerSection.splitLongerSectionBy(shorterSection))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.SECTION_NOT_ADDABLE.getMessage());
            }
        }

        @DisplayName("긴 구간과 이를 분리하려는 짧은 구간이 동일하거나, 겹치는 상-하행 역이 1개도 존재하지 않는 경우")
        @Nested
        class Context_invalidStations {

            @DisplayName("긴 구간과 짧은 구간이 동일하면 예외가 발생한다.")
            @Test
            void cannotSplitWhenSame() {
                Section longerSection = new Section(longerUpStation, longerDownStation, 15, 1L);
                Section shorterSection = new Section(longerUpStation, longerDownStation, 5, 1L);

                assertThatCode(() -> longerSection.splitLongerSectionBy(shorterSection))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.SECTION_NOT_ADDABLE.getMessage());
            }

            @DisplayName("긴 구간과 짧은 구간의 겹치는 상-하행 역이 1개도 존재하지 않으면 예외가 발생한다.")
            @Test
            void cannotSplitWhenDifferent() {
                Section longerSection = new Section(longerUpStation, longerDownStation, 15, 1L);
                Section shorterSection = new Section(longerDownStation, longerUpStation, 5, 1L);

                assertThatCode(() -> longerSection.splitLongerSectionBy(shorterSection))
                        .isInstanceOf(SubwayException.class)
                        .hasMessage(ExceptionStatus.SECTION_NOT_ADDABLE.getMessage());
            }
        }

        @DisplayName("긴 구간의 상행 종점과, 긴 구간을 분리하는 짧은 구간의 상행 종점이 같을 때")
        @Nested
        class Context_bothUpStationAreSame {

            @DisplayName("반환되는 구간의 상행 종점은 짧은 구간의 하행 종점이며, 하행 종점은 긴 구간의 하행 종점이다.")
            @Test
            void splitWhenBothUpStationAreSame() {
                Station shorterDownStation = new Station("잠실역");
                Section longerSection = new Section(longerUpStation, longerDownStation, 15, 1L);
                Section shorterSection = new Section(longerUpStation, shorterDownStation, 5, 1L);

                Section splitSection = longerSection.splitLongerSectionBy(shorterSection);
                Section compareSection = new Section(shorterDownStation, longerDownStation, 15 - 5, 1L);

                assertThat(splitSection).isEqualTo(compareSection);
            }
        }

        @DisplayName("긴 구간의 하행 종점과, 긴 구간을 분리하는 짧은 구간의 하행 종점이 같을 때")
        @Nested
        class Context_bothDownStationAreSame {

            @DisplayName("반환되는 구간의 상행 종점은 긴 구간의 상행 종점이며, 하행 종점은 짧은 구간의 상행 종점이다.")
            @Test
            void splitWhenBothDownStationAreSame() {
                Station shorterUpStation = new Station("잠실역");
                Section longerSection = new Section(longerUpStation, longerDownStation, 15, 1L);
                Section shorterSection = new Section(shorterUpStation, longerDownStation, 5, 1L);

                Section splitSection = longerSection.splitLongerSectionBy(shorterSection);
                Section compareSection = new Section(longerUpStation, shorterUpStation, 15 - 5, 1L);

                assertThat(splitSection).isEqualTo(compareSection);
            }
        }
    }
}
