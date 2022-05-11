package wooteco.subway.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.service.dto.SectionDto;

@DisplayName("SectionDao 는")
@JdbcTest
@Transactional
class SectionDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private SectionDao sectionDao;

    @BeforeEach
    void setup() {
        sectionDao = new SectionDao(jdbcTemplate, dataSource);
    }

    @DisplayName("구간을 저장할 수 있어야 한다.")
    @Test
    void save_Section() {
        final SectionDto sectionDto = new SectionDto(1L, 2L, 3);
        assertThat(sectionDao.save(4L, sectionDto)).isGreaterThanOrEqualTo(1);
    }

    @DisplayName("한 라인에 해당되는 모든 구간을 불러올 수 있어야 한다.")
    @Test
    void find_All_By_Id() {
        final SectionDto sectionDto = new SectionDto(1L, 2L, 3);
        final SectionDto sectionDto1 = new SectionDto(4L, 5L, 6);
        sectionDao.save(7L, sectionDto);
        sectionDao.save(7L, sectionDto1);

        final List<SectionDto> sectionDtos = sectionDao.findAllByLineId(7L);
        assertAll(
                () -> assertThat(sectionDtos.size()).isEqualTo(2),
                () -> assertThat(sectionDtos.get(0)).extracting("upStationId").isEqualTo(1L),
                () -> assertThat(sectionDtos.get(1)).extracting("upStationId").isEqualTo(4L)
        );

    }

    @DisplayName("구간을 삭제할 수 있어야 한다.")
    @Test
    void delete_Section() {
        final SectionDto sectionDto = new SectionDto(1L, 2L, 3);
        sectionDao.save(4L, sectionDto);
        assertThat(sectionDao.deleteById(4L, 1L)).isEqualTo(1);
    }
}
