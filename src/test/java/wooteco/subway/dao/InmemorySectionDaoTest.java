package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class InmemorySectionDaoTest {

    private final InmemorySectionDao jdbcSectionDao = InmemorySectionDao.getInstance();

    @AfterEach
    void afterEach() {
        jdbcSectionDao.clear();
    }

    @Test
    @DisplayName("Section 을 저장할 수 있다.")
    void save() {
        // given
        Line line = new Line(1L, "신분당선", "bg-red-600");
        Station upStation = new Station(1L, "오리");
        Station downStation = new Station(2L, "배카라");
        Section section = new Section(null, line, upStation, downStation, 1);

        // when
        Section savedSection = jdbcSectionDao.save(section);

        // then
        assertThat(savedSection.getId()).isNotNull();
    }

    @Nested
    @DisplayName("이미 존재하는 upStation, downStation 인지 확인할 수 있다.")
    class ExistByUpStationAndDownStation {

        private final Line line = new Line(1L, "신분당선", "bg-red-600");
        private final Station upStation = new Station(1L, "오리");
        private final Station downStation = new Station(2L, "배카라");

        @Test
        void isTrue() {
            Section section = new Section(null, line, upStation, downStation, 1);
            jdbcSectionDao.save(section);

            assertThat(jdbcSectionDao.existByUpStationAndDownStation(upStation, downStation)).isTrue();
        }

        @Test
        void isFalse() {
            assertThat(jdbcSectionDao.existByUpStationAndDownStation(upStation, downStation)).isFalse();
        }
    }
}
