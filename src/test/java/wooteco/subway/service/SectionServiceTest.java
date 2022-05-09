package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@Sql("/sectionTestSchema.sql")
class SectionServiceTest {

    public static final SectionRequest GIVEN_SECTION_REQ =
            new SectionRequest(1L, 2L, 6);

    private final SectionService sectionService;

    @Autowired
    public SectionServiceTest(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @Test
    @DisplayName("추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하는 경우 예외가 발생한다.")
    void bothStationExistException() {
        // given
        sectionService.save(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(() -> {
            sectionService.save(1L, GIVEN_SECTION_REQ);
        });

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행, 하행이 대상 노선에 둘 다 존재합니다.");
    }

    @Test
    @DisplayName("초기 등록이면 추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하지 않아도 예외가 발생하지 않는다.")
    void firstSave() {
        // given

        // when, then
        assertThatCode(() ->
                sectionService.save(1L, new SectionRequest(1L, 2L, 6)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("초기 등록이 아닌데 추가할 구간의 상행, 하행이 대상 노선에 둘 다 존재하지 않는 경우 예외가 발생한다.")
    void bothStationNotExistException() {
        // given
        sectionService.save(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(() -> {
            sectionService.save(1L, new SectionRequest(3L, 4L, 6));
        });

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행, 하행이 대상 노선에 둘 다 존재하지 않습니다.");
    }

    @Test
    @DisplayName("두 개의 역 중 하나가 노선에 상행으로 존재할 때, 등록할 구간의 거리가 기존 구간의 거리보다 크면 예외가 발생한다.")
    void upStationDistanceException() {
        // given
        sectionService.save(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(() -> {
            sectionService.save(1L, new SectionRequest(1L, 3L, 6));
        });

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("두 개의 역 중 하나가 노선에 하행으로 존재할 때, 등록할 구간의 거리가 기존 구간의 거리보다 크면 예외가 발생한다.")
    void downStationDistanceException() {
        // given
        sectionService.save(1L, GIVEN_SECTION_REQ);

        // when
        Throwable thrown = catchThrowable(() -> {
            sectionService.save(1L, new SectionRequest(3L, 2L, 6));
        });

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우, 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
    }
}