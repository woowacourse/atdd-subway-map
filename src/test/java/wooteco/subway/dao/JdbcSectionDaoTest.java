package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
class JdbcSectionDaoTest {

    private SectionDao jdbcSectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Section 을 저장할 수 있다.")
    void save() {
        Section section = new Section(null, 1L, 1L, 2L, 1);
        Section savedSection = jdbcSectionDao.save(section);

        assertThat(savedSection.getLineId()).isNotNull();
    }

    @Nested
    @DisplayName("이미 존재하는 upStation, downStation 인지 확인할 수 있다.")
    class ExistByUpStationAndDownStation {

        private final long upStationId = 1L;
        private final long downStationId = 2L;

        @Test
        void isTrue() {
            Section section = new Section(null, 1L, downStationId, upStationId, 1);
            jdbcSectionDao.save(section);

            assertThat(jdbcSectionDao.existByUpStationIdAndDownStationId(upStationId, downStationId)).isTrue();
        }

        @Test
        void isFalse() {
            assertThat(jdbcSectionDao.existByUpStationIdAndDownStationId(upStationId, downStationId)).isFalse();
        }
    }
}
