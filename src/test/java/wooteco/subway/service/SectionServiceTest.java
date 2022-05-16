package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.jdbc.JdbcSectionDao;
import wooteco.subway.service.dto.section.SectionRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
class SectionServiceTest {

    private SectionService sectionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(new JdbcSectionDao(jdbcTemplate));
    }

    @Test
    @DisplayName("구간을 추가한다")
    void createSection() {
        assertDoesNotThrow(() -> sectionService.create(new SectionRequestDto(1L, 1L, 2L, 10)));

        assertThat(sectionService.findAllByLineId(1L).getSections()).hasSize(1);
    }

    @Test
    @DisplayName("distance는 양수이다.")
    void createSectionFailCase1() {
        assertThatThrownBy(() -> sectionService.create(new SectionRequestDto(1L, 2L, 3L, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("같은 지하철역을 입력하면 추가할 수 없다.")
    void createSectionFailCase2() {
        assertThatThrownBy(() -> sectionService.create(new SectionRequestDto(1L, 2L, 2L, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("등록된 지하철역 구간을 재입력할 수 없다.")
    void createSectionFailCase3() {
        sectionService.create(new SectionRequestDto(1L, 2L, 3L, 10));

        assertThatThrownBy(() -> sectionService.create(new SectionRequestDto(1L, 2L, 3L, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("등록된 지하철역 양 끝에 추가할 수 있다.")
    void createSectionFailCase4() {
        sectionService.create(new SectionRequestDto(1L, 2L, 3L, 10));
        sectionService.create(new SectionRequestDto(1L, 3L, 4L, 10));

        assertAll(
                () -> assertDoesNotThrow(() -> sectionService.create(new SectionRequestDto(1L, 1L, 2L, 10))),
                () -> assertDoesNotThrow(() -> sectionService.create(new SectionRequestDto(1L, 4L, 5L, 10))),
                () -> assertThat(sectionService.findAllByLineId(1L).getSections()).hasSize(4)
        );
    }

    @Test
    @DisplayName("등록된 지하철역 중간에 추가할 수 있다.")
    void createSectionFailCase5() {
        sectionService.create(new SectionRequestDto(1L, 1L, 4L, 10));

        assertAll(
                () -> assertDoesNotThrow(() -> sectionService.create(new SectionRequestDto(1L, 1L, 2L, 3))),
                () -> assertDoesNotThrow(() -> sectionService.create(new SectionRequestDto(1L, 3L, 4L, 3))),
                () -> assertThat(sectionService.findAllByLineId(1L).getSections()).hasSize(3)
        );
    }

    @Test
    @DisplayName("추가한 구간이 기존거리를 초과하면 추가할 수 없다.")
    void createSectionFailCase6() {
        sectionService.create(new SectionRequestDto(1L, 1L, 4L, 10));

        assertAll(
                () -> assertThatThrownBy(() -> sectionService.create(new SectionRequestDto(1L, 1L, 2L, 20)))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> sectionService.create(new SectionRequestDto(1L, 3L, 4L, 20)))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @Test
    @DisplayName("추가할 구간의 지하철역은 기존에 구간에 등록이 되어야 한다.")
    void createSectionFailCase7() {
        sectionService.create(new SectionRequestDto(1L, 1L, 2L, 10));

        assertThatThrownBy(() -> sectionService.create(new SectionRequestDto(1L, 3L, 4L, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("끝에 있는 지하철역을 삭제할 수 있다.")
    void deleteOneSection() {
        sectionService.create(new SectionRequestDto(1L, 1L, 2L, 10));
        sectionService.create(new SectionRequestDto(1L, 2L, 3L, 10));
        sectionService.create(new SectionRequestDto(1L, 3L, 4L, 10));

        assertAll(
                () -> assertDoesNotThrow(() -> sectionService.delete(1L, 1L)),
                () -> assertThat(sectionService.findAllByLineId(1L).getSections()).hasSize(2)
        );
    }

    @Test
    @DisplayName("중간의 지하철역을 삭제할 수 있다.")
    void deleteMiddleSection() {
        sectionService.create(new SectionRequestDto(1L, 1L, 2L, 10));
        sectionService.create(new SectionRequestDto(1L, 2L, 3L, 10));
        sectionService.create(new SectionRequestDto(1L, 3L, 4L, 10));

        assertAll(
                () -> assertDoesNotThrow(() -> sectionService.delete(1L, 2L)),
                () -> assertDoesNotThrow(() -> sectionService.delete(1L, 3L)),
                () -> assertThat(sectionService.findAllByLineId(1L).getSections()).hasSize(1)
        );
    }
}