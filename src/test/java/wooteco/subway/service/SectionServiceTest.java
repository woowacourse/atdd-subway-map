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
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private SectionService sectionService;
    private SectionDao sectionDao;

    private StationResponse savedStation1;
    private StationResponse savedStation2;
    private StationResponse savedStation3;
    private StationResponse savedStation4;
    private LineResponse savedLine;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate, dataSource);
        StationService stationService = new StationService(new StationDao(jdbcTemplate, dataSource));
        LineService lineService = new LineService(new LineDao(jdbcTemplate, dataSource),
                new StationDao(jdbcTemplate, dataSource), sectionDao);
        sectionService = new SectionService(new LineDao(jdbcTemplate, dataSource),
                new StationDao(jdbcTemplate, dataSource), sectionDao);

        savedStation1 = stationService.create(new StationRequest("선릉역"));
        savedStation2 = stationService.create(new StationRequest("선정릉역"));
        savedStation3 = stationService.create(new StationRequest("한티역"));
        savedStation4 = stationService.create(new StationRequest("모란역"));

        savedLine = lineService.create(new LineRequest("분당선", "yellow", savedStation1.getId(),
                savedStation2.getId(), 10));
    }

    @DisplayName("구간 생성 테스트")
    @Nested
    class CreateTest {
        @DisplayName("상행 종점에 역을 등록한다.")
        @Test
        void createStationAtLastUp() {
            SectionRequest request = new SectionRequest(savedStation3.getId(), savedStation1.getId(), 7);

            sectionService.create(savedLine.getId(), request);

            List<Section> sections = sectionDao.findAllByLineId(savedLine.getId());
            assertThat(sections).hasSize(2);
        }

        @DisplayName("하행 종점에 역을 등록한다.")
        @Test
        void createStationAtLastDown() {
            SectionRequest request = new SectionRequest(savedStation2.getId(), savedStation3.getId(), 7);

            sectionService.create(savedLine.getId(), request);

            List<Section> sections = sectionDao.findAllByLineId(savedLine.getId());
            assertThat(sections).hasSize(2);
        }

        @DisplayName("구간 사이에 새로운 구간을 등록한다.")
        @Test
        void createStationAtMiddle() {
            SectionRequest request = new SectionRequest(savedStation1.getId(), savedStation3.getId(), 7);

            sectionService.create(savedLine.getId(), request);

            List<Section> sections = sectionDao.findAllByLineId(savedLine.getId());
            assertThat(sections).hasSize(2);
        }

        @DisplayName("구간 사이에 새로운 구간을 등록할 때, 기존 구간보다 큰 거리로 생성을 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithLongerDistance() {
            SectionRequest request = new SectionRequest(savedStation1.getId(), savedStation3.getId(), 10);

            assertThatThrownBy(() -> sectionService.create(savedLine.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
        }

        @DisplayName("존재하지 않는 노선에 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithNonExistLine() {
            SectionRequest request = new SectionRequest(savedStation1.getId(), savedStation3.getId(), 7);

            assertThatThrownBy(() -> sectionService.create(1000L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("존재하지 않는 노선입니다.");
        }

        @DisplayName("존재하지 않는 역으로 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithNonExistStation() {
            SectionRequest request = new SectionRequest(savedStation1.getId(), 100L, 7);

            assertThatThrownBy(() -> sectionService.create(savedLine.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("등록되지 않은 역으로는 구간을 만들 수 없습니다.");
        }

        @DisplayName("노선에 포함되지 않은 두 역으로 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionNotContainStationInLine() {
            SectionRequest request = new SectionRequest(savedStation3.getId(), savedStation4.getId(), 7);

            assertThatThrownBy(() -> sectionService.create(savedLine.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("구간을 추가하기 위해서는 노선에 들어있는 역이 필요합니다.");
        }

        @DisplayName("이미 노선에 존재하는 두 역으로 구간을 만들려고 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionContainExistTwoStationInLine() {
            SectionRequest request = new SectionRequest(savedStation1.getId(), savedStation2.getId(), 7);

            assertThatThrownBy(() -> sectionService.create(savedLine.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        @DisplayName("0이하의 거리로 구간 생성을 시도하면 예외가 발생한다.")
        @Test
        void throwsExceptionWithNegativeDistance() {
            SectionRequest request = new SectionRequest(savedStation1.getId(), savedStation3.getId(), 0);

            assertThatThrownBy(() -> sectionService.create(savedLine.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageMatching("구간 사이의 거리는 0보다 커야합니다.");
        }
    }
}
