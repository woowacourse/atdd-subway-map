package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SectionTest {

    private Station upStation1;
    private Station downStation1;
    private Station upStation2;
    private Station downStation2;

    @BeforeEach
    void setUp() {
        upStation1 = new Station("강남역");
        downStation1 = new Station("역삼역");
        upStation2 = new Station("선릉역");
        downStation2 = new Station("삼성역");
    }

    @DisplayName("기존 거리보다 더 크거나 같은지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"10,true", "11,true", "9,false", "1,false"})
    void isGreaterOrEqualTo(final int distance, final boolean expected) {
        final Section section1 = new Section(upStation1, downStation1, distance);
        final Section section2 = new Section(upStation2, downStation2, 10);

        assertThat(section1.isGreaterOrEqualTo(section2)).isEqualTo(expected);
    }

    @DisplayName("하행역들로 구간을 생성한다.")
    @Test
    void createSectionByUpStation() {
        final Section section1 = new Section(upStation1, downStation1, 5);
        final Section section2 = new Section(upStation1, downStation2, 10);

        final Section newSection = section1.createSectionByUpStation(section2);

        assertThat(newSection).usingRecursiveComparison()
                .isEqualTo(new Section(
                        section1.getId(),
                        section1.getUpStation(),
                        section2.getUpStation(),
                        section1.getDistance() - section2.getDistance(),
                        section1.getLineId()));
    }

    @DisplayName("상행역들로 구간을 생성한다.")
    @Test
    void createSectionByDownStation() {
        final Section section1 = new Section(upStation1, downStation1, 5);
        final Section section2 = new Section(upStation2, downStation1, 10);

        final Section newSection = section1.createSectionByDownStation(section2);

        assertThat(newSection).usingRecursiveComparison()
                .isEqualTo(new Section(
                        section1.getId(),
                        section2.getDownStation(),
                        section1.getDownStation(),
                        section1.getDistance() - section2.getDistance(),
                        section1.getLineId()));
    }

    @DisplayName("(갈래길일 경우) 상행역이 일치한다면 하행역을 기준으로 새로운 구간을 생성한다.")
    @Test
    void createSectionInBetweenWhenFirstDownAndSecondUpIsSame() {
        final Section section1 = new Section(upStation1, downStation1, 5);
        final Section section2 = new Section(upStation1, downStation2, 10);

        final Section newSection = section1.createSectionInBetween(section2);

        assertThat(newSection).usingRecursiveComparison()
                .isEqualTo(section1.createSectionByDownStation(section2));
    }

    @DisplayName("(갈래길일 경우) 하행역이 일치한다면 상행역을 기준으로 새로운 구간을 생성한다.")
    @Test
    void createSectionInBetweenWhenSecondUpAndFirstDownIsSame() {
        final Section section1 = new Section(upStation1, downStation1, 5);
        final Section section2 = new Section(upStation2, downStation1, 10);

        final Section newSection = section1.createSectionInBetween(section2);

        assertThat(newSection).usingRecursiveComparison()
                .isEqualTo(section1.createSectionByUpStation(section2));
    }

    @DisplayName("첫 번째 구간의 상행역과 두 번째 구간의 하행역으로 새로운 구간을 생성한다.")
    @Test
    void merge() {
        final Section section1 = new Section(upStation1, downStation1, 5);
        final Section section2 = new Section(upStation2, downStation2, 10);

        final Section mergedSection = section1.merge(section2);

        assertThat(mergedSection).usingRecursiveComparison()
                .isEqualTo(new Section(upStation1, downStation2, 15));
    }
}
