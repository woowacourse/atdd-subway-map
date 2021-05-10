package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.section.exception.SectionDistanceException;
import wooteco.subway.section.exception.SectionInclusionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("truncate table SECTION");
        jdbcTemplate.execute("alter table SECTION alter column ID restart with 1");
    }

    @Test
    @DisplayName("정상적인 중간 구간 저장")
    public void saveSectionWithNormalCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(2L, 10L, 1));
        sectionService.save(sectionDto);

        assertThat(sectionDao.findSectionByUpStationId(2L).get())
                .usingRecursiveComparison()
                .isEqualTo(new Section(3L, 1L, 2L, 10L, 1));
    }

    @Test
    @DisplayName("역 사이의 거리가 기존 구간의 거리 이상일 경우의 중간 구간 저장")
    public void saveSectionWithDistanceExceptionCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(2L, 10L, 10));
        SectionDto sectionDto2 = SectionDto.of(1L, new SectionRequest(2L, 10L, 11));

        assertThatThrownBy(() -> sectionService.save(sectionDto))
                .isInstanceOf(SectionDistanceException.class);
        assertThatThrownBy(() -> sectionService.save(sectionDto2))
                .isInstanceOf(SectionDistanceException.class);
    }

    @Test
    @DisplayName("상행 종점 구간 등록")
    public void saveSectionWithUpEndPointCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(10L, 1L, 1));
        sectionService.save(sectionDto);

        assertThat(sectionDao.findSectionByUpStationId(10L).get())
                .usingRecursiveComparison()
                .isEqualTo(new Section(2L, 1L, 10L, 1L, 1));
    }

    @Test
    @DisplayName("하행 종점 구간 등록")
    public void saveSectionWithDownEndPointCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(2L, 10L, 1));
        sectionService.save(sectionDto);

        assertThat(sectionDao.findSectionByUpStationId(2L).get())
                .usingRecursiveComparison()
                .isEqualTo(new Section(2L, 1L, 2L, 10L, 1));
    }

    @Test
    @DisplayName("구간의 양 역이 노선에 모두 포함될 경우의 구간 등록")
    public void saveSectionWithBothStationContainCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(1L, 2L, 1));

        assertThatThrownBy(() -> sectionService.save(sectionDto))
                .isInstanceOf(SectionInclusionException.class);
    }

    @Test
    @DisplayName("구간의 양 역이 노선에 아무것도 포함된 것이 없을 경우의 구간 등록")
    public void saveSectionWithNeitherStationContainCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(10L, 20L, 1));

        assertThatThrownBy(() -> sectionService.save(sectionDto))
                .isInstanceOf(SectionInclusionException.class);
    }
}