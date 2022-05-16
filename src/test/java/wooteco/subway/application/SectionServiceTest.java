package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@DisplayName("Section Service 클래스")
class SectionServiceTest {

    private final SectionService sectionService;

    @Mock
    private LineDao lineDao;

    @Mock
    private StationDao stationDao;

    private SectionDao<Section> sectionDao;

    public SectionServiceTest() {
        MockitoAnnotations.openMocks(this);
        this.sectionDao = new FakeSectionDao();
        this.sectionService = new SectionService(sectionDao, stationDao, lineDao);
    }

    @Nested
    @DisplayName("addSection 메소드는")
    class DescribeAddSection {

        Section subject(Long upStationId, Long downStationId, int distance) {
            return sectionService.addSection(1L, upStationId, downStationId, distance);
        }

        @BeforeEach
        void setUp() {
            given(stationDao.findById(1L)).willReturn(new Station(1L, "강남역"));
            given(stationDao.findById(2L)).willReturn(new Station(2L, "역삼역"));
            given(stationDao.findById(3L)).willReturn(new Station(3L, "에덴역"));
        }

        @Nested
        @DisplayName("상행 종점 구간 추가를 요청받으면")
        class ContextWithLastUpStation {

            @Test
            @DisplayName("아이디가 포함된 새로운 구간을 반환한다.")
            void it_returns_new_section() {
                Section section = subject(3L, 1L, 5);
                assertThat(section.getUpStation().getName()).isEqualTo("에덴역");
            }
        }

        @Nested
        @DisplayName("하행 종점 구간 추가를 요청받으면")
        class ContextWithLastDownStation {

            @Test
            @DisplayName("아이디가 포함된 새로운 구간을 반환한다.")
            void it_returns_new_section() {
                Section section = subject(2L, 3L, 5);
                assertThat(section.getDownStation().getName()).isEqualTo("에덴역");
            }
        }

        @Nested
        @DisplayName("상행 가지 구간 추가를 요청받으면")
        class ContextWithBranchUpStation {

            @Test
            @DisplayName("아이디가 포함된 새로운 구간을 반환한다.")
            void it_returns_new_section() {
                Section section = subject(1L, 3L, 3);
                assertThat(section.getDownStation().getName()).isEqualTo("에덴역");
            }
        }

        @Nested
        @DisplayName("거리가 더 큰 상행 가지 구간 추가를 요청받으면")
        class ContextWithBranchUpStationLongerDistance {

            private final int longerDistance = 6;

            @Test
            @DisplayName("기존 거리와 요청받은 거리를 메세지에 포함한 예외를 반환한다.")
            void it_throws_exception() {
                assertThatThrownBy(
                        () -> subject(1L, 3L, longerDistance))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("5")
                        .hasMessageContaining(String.valueOf(longerDistance));
            }
        }

        @Nested
        @DisplayName("하행 가지 구간 추가를 요청받으면")
        class ContextWithBranchDownStation {

            @Test
            @DisplayName("아이디가 포함된 새로운 구간을 반환한다.")
            void it_returns_new_section() {
                Section section = subject(3L, 2L, 3);
                assertThat(section.getUpStation().getName()).isEqualTo("에덴역");
            }
        }

        @Nested
        @DisplayName("거리가 더 큰 하행 가지 구간 추가를 요청받으면")
        class ContextWithBranchDownStationLongerDistance {

            private final int longerDistance = 6;

            @Test
            @DisplayName("기존 거리와 요청받은 거리를 메세지에 포함한 예외를 반환한다.")
            void it_throws_exception() {
                assertThatThrownBy(
                        () -> subject(3L, 2L, longerDistance))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("5")
                        .hasMessageContaining(String.valueOf(longerDistance));
            }
        }

        @Nested
        @DisplayName("기존에 있던 두 역 추가를 요청받으면")
        class ContextWithExistStations {

            @Test
            @DisplayName("순서대로 기존에 존재하던 역 이름을 메세지에 포함한 예외를 반환한다.")
            void it_throws_exception_with_asc() {
                assertThatThrownBy(
                        () -> subject(1L, 2L, 3))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("강남역")
                        .hasMessageContaining("역삼역");
            }

            @Test
            @DisplayName("반대로 기존에 존재하던 역 이름을 메세지에 포함한 예외를 반환한다.")
            void it_throws_exception_with_desc() {
                assertThatThrownBy(
                        () -> subject(2L, 1L, 5))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("강남역")
                        .hasMessageContaining("역삼역");
            }
        }

        @Nested
        @DisplayName("기존에 없던 두 역 추가를 요청받으면")
        class ContextWithNotExistStations {

            @Test
            @DisplayName("추가할 수 없다는 메세지와 함께 예외를 반환한다.")
            void it_throws_exception() {

                given(stationDao.findById(4L)).willReturn(new Station(4L, "아자르역"));
                assertThatThrownBy(
                        () -> subject(3L, 4L, 3))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("추가할 수");
            }
        }
    }

    @Nested
    @DisplayName("deleteSection 메소드는")
    class DescribeDeleteSection {

        int subject(Long lineId, Long stationId) {
            return sectionService.deleteSection(lineId, stationId);
        }

        @Nested
        @DisplayName("line Id 와 station Id 로 구간 삭제 요청을 받으면")
        class ContextWithLineIdAndStationId {

            @Test
            @DisplayName("삭제된 열의 개수를 반환한다.")
            void it_returns_affected_query_count() {
                int affectedCount = subject(3L, 2L);
                assertThat(affectedCount).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("삭제 후 남은 구간이 없는 구간 삭제 요청을 받으면")
        class ContextWithNotLeftSection {

            @Test
            @DisplayName("최소한의 메세지를 포함한 예외를 반환한다.")
            void it_throws_exception() {
                assertThatThrownBy(
                        () -> subject(1L, 2L)
                )
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("최소한");
            }
        }
    }
}
