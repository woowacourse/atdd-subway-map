package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.*;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@SuppressWarnings("NonAsciiCharacters")
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private SectionService sectionService;
    private SectionDao sectionDao;

    private StationResponse 선릉역;
    private StationResponse 선정릉역;
    private StationResponse 한티역;
    private StationResponse 모란역;
    private StationResponse 기흥역;
    private LineResponse 분당선;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate, dataSource);
        StationService stationService = new StationService(new StationDao(jdbcTemplate, dataSource));
        LineService lineService = new LineService(new LineDao(jdbcTemplate, dataSource),
                new StationDao(jdbcTemplate, dataSource), sectionDao);
        sectionService = new SectionService(new LineDao(jdbcTemplate, dataSource),
                new StationDao(jdbcTemplate, dataSource), sectionDao);

        선릉역 = stationService.create(new StationRequest("선릉역"));
        선정릉역 = stationService.create(new StationRequest("선정릉역"));
        한티역 = stationService.create(new StationRequest("한티역"));
        모란역 = stationService.create(new StationRequest("모란역"));
        기흥역 = stationService.create(new StationRequest("기흥역"));

        분당선 = lineService.create(new LineRequest("분당선", "yellow", 선릉역.getId(),
                선정릉역.getId(), 10));
        sectionDao.insert(new Section(분당선.getId(), 선정릉역.getId(), 한티역.getId(), 10));
    }

    @DisplayName("구간 생성 테스트")
    @Nested
    class CreateTest {
        @DisplayName("상행 종점에 역을 등록한다.")
        @Test
        void createStationAtLastUp() {
            SectionRequest request = new SectionRequest(모란역.getId(), 선릉역.getId(), 7);

            sectionService.create(분당선.getId(), request);

            List<Section> sections = sectionDao.findAllByLineId(분당선.getId());
            assertThat(sections).hasSize(3);
        }

        @DisplayName("하행 종점에 역을 등록한다.")
        @Test
        void createStationAtLastDown() {
            SectionRequest request = new SectionRequest(한티역.getId(), 모란역.getId(), 7);

            sectionService.create(분당선.getId(), request);

            List<Section> sections = sectionDao.findAllByLineId(분당선.getId());
            assertThat(sections).hasSize(3);
        }

        @DisplayName("구간 사이에 새로운 구간을 등록한다.")
        @Test
        void createStationAtMiddle() {
            SectionRequest request = new SectionRequest(선릉역.getId(), 모란역.getId(), 7);

            sectionService.create(분당선.getId(), request);

            List<Section> sections = sectionDao.findAllByLineId(분당선.getId());
            assertThat(sections).hasSize(3);
        }

        @DisplayName("구간 사이에 새로운 구간을 등록할 때, 기존 구간보다 큰 거리로 생성을 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithLongerDistance() {
            SectionRequest request = new SectionRequest(선릉역.getId(), 모란역.getId(), 10);

            assertThatThrownBy(() -> sectionService.create(분당선.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
        }

        @DisplayName("존재하지 않는 노선에 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithNonExistLine() {
            SectionRequest request = new SectionRequest(선릉역.getId(), 모란역.getId(), 7);

            assertThatThrownBy(() -> sectionService.create(1000L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("존재하지 않는 노선입니다.");
        }

        @DisplayName("존재하지 않는 역으로 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithNonExistStation() {
            SectionRequest request = new SectionRequest(선릉역.getId(), 100L, 7);

            assertThatThrownBy(() -> sectionService.create(분당선.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("등록되지 않은 역으로는 구간을 만들 수 없습니다.");
        }

        @DisplayName("노선에 포함되지 않은 두 역으로 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionNotContainStationInLine() {
            SectionRequest request = new SectionRequest(모란역.getId(), 기흥역.getId(), 7);

            assertThatThrownBy(() -> sectionService.create(분당선.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("구간을 추가하기 위해서는 노선에 들어있는 역이 필요합니다.");
        }

        @DisplayName("이미 노선에 존재하는 두 역으로 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionContainExistTwoStationInLine() {
            SectionRequest request = new SectionRequest(선릉역.getId(), 선정릉역.getId(), 7);

            assertThatThrownBy(() -> sectionService.create(분당선.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        @DisplayName("0이하의 거리로 구간 생성을 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithNegativeDistance() {
            SectionRequest request = new SectionRequest(선릉역.getId(), 모란역.getId(), 0);

            assertThatThrownBy(() -> sectionService.create(분당선.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("구간 사이의 거리는 0보다 커야합니다.");
        }
    }

    @DisplayName("구간 삭제 테스트")
    @Nested
    class DeleteTest {

        @DisplayName("상행 종점 역이 포함된 구간을 올바르게 삭제한다.")
        @Test
        void deleteSectionAtLastUpStation() {
            sectionService.delete(분당선.getId(), 선릉역.getId());

            List<Section> changedSections = sectionDao.findAllByLineId(분당선.getId());
            assertThat(changedSections).hasSize(1);
        }

        @DisplayName("하행 종점 역이 포함된 구간을 올바르게 삭제한다.")
        @Test
        void deleteSectionAtLastDownStation() {
            sectionService.delete(분당선.getId(), 한티역.getId());

            List<Section> changedSections = sectionDao.findAllByLineId(분당선.getId());
            assertThat(changedSections).hasSize(1);
        }

        @DisplayName("중간 역이 포함된 구간을 올바르게 삭제한다.")
        @Test
        void deleteSectionAtMiddleStation() {
            sectionService.delete(분당선.getId(), 선정릉역.getId());

            List<Section> changedSections = sectionDao.findAllByLineId(분당선.getId());
            assertThat(changedSections).hasSize(1);
        }

        @DisplayName("구간이 하나만 남았을 경우 예외가 발생한다.")
        @Test
        void throwsExceptionWithOneRemainSection() {
            sectionService.delete(분당선.getId(), 선릉역.getId());

            assertThatThrownBy(() -> sectionService.delete(분당선.getId(), 선정릉역.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("구간이 하나인 노선에서는 구간 삭제가 불가합니다.");
        }

        @DisplayName("현재 라인에 존재하지 않는 역으로 삭제 시도시 예외가 발생한다.")
        @Test
        void throwsExceptionWithNotExistStationInLine() {
            assertThatThrownBy(() -> sectionService.delete(분당선.getId(), 모란역.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("현재 라인에 존재하지 않는 역입니다.");
        }
    }
}
