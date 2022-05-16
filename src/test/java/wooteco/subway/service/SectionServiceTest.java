package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@Sql("/sectionInitSchema.sql")
class SectionServiceTest {

    public static final SectionRequest GIVEN_SECTION_REQ =
        new SectionRequest(1L, 2L, 6);

    private final SectionService sectionService;

    @Autowired
    public SectionServiceTest(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @Test
    @DisplayName("초기 등록이면 추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하지 않아도 예외가 발생하지 않는다.")
    void firstSave() {
        // given

        // when, then
        assertThatCode(() ->
            sectionService.firstSave(1L, GIVEN_SECTION_REQ))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하는 경우 예외가 발생한다.")
    void bothStationExistException() {
        // given
        sectionService.firstSave(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(() -> sectionService.save(1L, GIVEN_SECTION_REQ));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상행, 하행이 대상 노선에 둘 다 존재합니다.");
    }

    @Test
    @DisplayName("초기 등록이 아닌데 추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하지 않는 경우 예외가 발생한다.")
    void bothStationNotExistException() {
        // given
        sectionService.firstSave(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(
            () -> sectionService.save(1L, new SectionRequest(3L, 4L, 6)));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상행, 하행이 대상 노선에 둘 다 존재하지 않습니다.");
    }

    @Test
    @DisplayName("두 개의 역 중 하나가 노선에 상행으로 존재할 때, 등록할 구간의 거리가 기존 구간의 거리보다 크면 예외가 발생한다.")
    void upStationDistanceException() {
        // given
        sectionService.firstSave(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(
            () -> sectionService.save(1L, new SectionRequest(1L, 3L, 6)));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("두 개의 역 중 하나가 노선에 하행으로 존재할 때, 등록할 구간의 거리가 기존 구간의 거리보다 크면 예외가 발생한다.")
    void downStationDistanceException() {
        // given
        sectionService.firstSave(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(
            () -> sectionService.save(1L, new SectionRequest(3L, 2L, 6)));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("등록하려는 구간의 상행 기존 구간의 상행과 겹치는 경우 구간 등록 테스트")
    void saveTest() {
        // given
        sectionService.firstSave(1L, new SectionRequest(1L, 3L, 6));

        // when
        sectionService.save(1L, new SectionRequest(1L, 2L, 3));

        // then
        assertThat(sectionService.findAllStationByLineId(1L))
            .containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("등록하려는 구간의 하행이 기존 구간의 하행과 겹치는 경우 구간 등록 테스트")
    void saveTest2() {
        // given
        sectionService.firstSave(1L, new SectionRequest(1L, 3L, 6));

        // when
        sectionService.save(1L, new SectionRequest(2L, 3L, 3));

        // then
        assertThat(sectionService.findAllStationByLineId(1L))
            .containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("등록하려는 구간의 하행이 기존 구간의 상행과 겹치는 경우 구간 등록 테스트")
    void saveTest3() {
        // given
        sectionService.firstSave(1L, new SectionRequest(2L, 3L, 6));

        // when
        sectionService.save(1L, new SectionRequest(1L, 2L, 3));

        // then
        assertThat(sectionService.findAllStationByLineId(1L))
            .containsExactly(1L, 2L, 3L);

    }

    @Test
    @DisplayName("등록하려는 구간의 상행이 기존 구간의 하행과 겹치는 경우 구간 등록 테스트")
    void saveTest4() {
        // given
        sectionService.firstSave(1L, new SectionRequest(1L, 2L, 6));

        // when
        sectionService.save(1L, new SectionRequest(2L, 3L, 3));

        // then
        assertThat(sectionService.findAllStationByLineId(1L))
            .containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("상행종점을 삭제한다.")
    void deleteUpEndStation() {
        // given
        sectionService.firstSave(1L, new SectionRequest(1L, 3L, 6));
        sectionService.save(1L, new SectionRequest(1L, 2L, 3));

        // when
        sectionService.deleteByLineIdAndStationId(1L, 1L);

        // then
        assertThat(sectionService.findAllStationByLineId(1L))
            .containsExactly(2L, 3L);
    }

    @Test
    @DisplayName("하행종점을 삭제한다.")
    void deleteDownEndStation() {
        // given
        sectionService.firstSave(1L, new SectionRequest(1L, 3L, 6));
        sectionService.save(1L, new SectionRequest(1L, 2L, 3));

        // when
        sectionService.deleteByLineIdAndStationId(1L, 3L);

        // then
        assertThat(sectionService.findAllStationByLineId(1L))
            .containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("양방향 구간이 모두 존재하는 지하철 역을 삭제한다.")
    void deleteStation() {
        // given
        sectionService.firstSave(1L, new SectionRequest(1L, 3L, 6));
        sectionService.save(1L, new SectionRequest(1L, 2L, 3));

        // when
        sectionService.deleteByLineIdAndStationId(1L, 2L);

        // then
        assertThat(sectionService.findAllStationByLineId(1L))
            .containsExactly(1L, 3L);
    }
}