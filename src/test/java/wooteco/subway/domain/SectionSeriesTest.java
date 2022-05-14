package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.domain.fixture.SectionFixture.*;
import static wooteco.subway.domain.fixture.StationFixture.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionSeriesTest {

    @Test
    @DisplayName("상행에서 하행을 잇는다.")
    public void findUpToDownSection() {
        // given
        SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));
        // when
        sectionSeries.add(new Section(3L, STATION_B, STATION_X, new Distance(3)));
        // then

        assertThat(sectionSeries.getSections()).hasSize(3);
    }

    @Test
    @DisplayName("하행에서 상행을 잇는다.")
    public void findDownToUpSection() {
        // given
        SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));
        // when
        sectionSeries.add(new Section(3L, STATION_X, STATION_B, new Distance(3)));
        // then
        assertThat(sectionSeries.getSections()).hasSize(3);
    }

    @Test
    @DisplayName("상행에 종점을 잇는다.")
    public void findUpToEndSection() {
        // given
        SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));

        // when
        sectionSeries.add(new Section(3L, STATION_X, STATION_A, new Distance(3)));

        // then
        assertThat(sectionSeries.getSections()).hasSize(3);
    }

    @Test
    @DisplayName("하행에 종점을 잇는다.")
    public void findDownToEndSection() {
        // given
        SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));

        // when
        sectionSeries.add(new Section(3L, STATION_C, STATION_X, new Distance(3)));

        // then
        assertThat(sectionSeries.getSections()).hasSize(3);
    }

    @Test
    @DisplayName("중복 노선을 등록하면 예외를 던진다.")
    public void throwsExceptionWithDuplicatedSection() {
        // given
        final SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));
        // when
        final Section newSection = new Section(3L, STATION_A, STATION_B, new Distance(3));
        // then
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> sectionSeries.add(newSection));
    }

    @Test
    @DisplayName("기존보다 더 넓은 범위의 중복 노선을 등록하면 예외를 던진다.")
    public void throwsExceptionWithBroadDuplicatedSection() {
        // given
        final SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));
        // when
        final Section newSection = new Section(3L, STATION_A, STATION_C, new Distance(3));
        // then
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> sectionSeries.add(newSection));
    }

    @Test
    @DisplayName("관련없는 노선을 등록하려는 경우 예외를 던진다.")
    public void throwsExceptionWithUnrelatedSection() {
        // given
        final SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));
        // when
        final Section newSection = new Section(3L, STATION_X, STATION_Y, new Distance(3));
        // then
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> sectionSeries.add(newSection));
    }

    @Test
    @DisplayName("등록하려는 구간 거리가 기존보다 짧거나 같으면 예외를 던진다.")
    public void throwsExceptionWithShorterDistance() {
        // given
        final SectionSeries sectionSeries = new SectionSeries(List.of(SECTION_AB, SECTION_BC));
        // when
        final Section newSection = new Section(3L, STATION_A, STATION_X, new Distance(100));
        // then
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> sectionSeries.add(newSection));
    }
}