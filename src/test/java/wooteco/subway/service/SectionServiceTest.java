package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("SectionService 클래스")
public class SectionServiceTest {

    private final Long lineId = 1L;
    private final Long upEndStation = 1L;
    private final Long middleStation = 2L;
    private final Long downEndStation = 3L;

    private final Section firstSection = new Section(1L, lineId, upEndStation, middleStation, 10);
    private final Section secondSection = new Section(2L, lineId, middleStation, downEndStation, 10);

    private final Sections sections = new Sections(List.of(firstSection, secondSection));

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private SectionDao sectionDao;


    @Nested
    @DisplayName("addSection 메서드는")
    class Describe_addSection {

        @Nested
        @DisplayName("상행 종점 구간을 등록할 경우")
        class Context_add_upStation {

            SectionRequest sectionRequest = new SectionRequest(4L, upEndStation, 10);

            @Test
            @DisplayName("입력 받은 Section을 저장한다.")
            void it_verify_addUpEndSection_success() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                sectionService.addSection(1L, sectionRequest);
                // then
                verify(sectionDao).save(new Section(4L, upEndStation, 10));
            }
        }

        @Nested
        @DisplayName("하행 종점 구간을 등록할 경우")
        class Context_add_downStation {

            SectionRequest sectionRequest = new SectionRequest(downEndStation, 4L, 10);

            @Test
            @DisplayName("입력 받은 Section을 저장한다.")
            void it_verify_addDownEndSection_success() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                sectionService.addSection(1L, sectionRequest);
                // then
                verify(sectionDao).save(new Section(downEndStation, 4L, 10));
            }
        }

        @Nested
        @DisplayName("구간 사이에 상행 지하철 역 기준으로 새로운 구간을 등록할 경우")
        class Context_add_WayPoint {

            Long wayPointStationId = 4L;
            int successDistance = 5;
            int failDistance = 10;

            SectionRequest successSectionRequest = new SectionRequest(1L, wayPointStationId, successDistance);
            SectionRequest failSectionRequest = new SectionRequest(1L, wayPointStationId, failDistance);

            @Test
            @DisplayName("기존에 있던 구간을 분리하여 저장한다.")
            void it_verify_save_splitSection() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                sectionService.addSection(lineId, successSectionRequest);
                // then
                verify(sectionDao).delete(1L);
                verify(sectionDao).save(new Section(1L, wayPointStationId, 5));
                verify(sectionDao).save(new Section(wayPointStationId, middleStation, 5));
            }

            @Test
            @DisplayName("길이가 크거나 같다면 예외를 발생한다.")
            void it_verify_longerThanOriginSection_exception() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                // then
                assertThatThrownBy(() -> sectionService.addSection(1L, failSectionRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("[ERROR] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다.");
            }
        }

        @Nested
        @DisplayName("등록하려는 구간의 상, 하행 지하철역과 동일한 구간이 노선 내에 있는 경우")
        class Context_add_existSameSection_InLine {

            @Test
            @DisplayName("예외를 발생한다.")
            void it_verify_existSameSection_Exception() {
                // given
                SectionRequest sectionRequest = new SectionRequest(upEndStation, downEndStation, 10);
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                // then
                assertThatThrownBy(() -> sectionService.addSection(1L, sectionRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("[ERROR] 상,하행 역이 구간에 모두 포함된 경우 추가할 수 없습니다.");
            }
        }

        @Nested
        @DisplayName("새로운 구간을 등록할 때 일치하는 상, 하행 지하철 역이 없는 경우")
        class Context_add_noExistBothStation_InLine {

            SectionRequest sectionRequest = new SectionRequest(5L, 6L, 10);

            @Test
            @DisplayName("예외를 발생한다.")
            void it_verify_noExistStations_Exception() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                // then
                assertThatThrownBy(() -> sectionService.addSection(1L, sectionRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("[ERROR] 상,하행 역이 모두 구간에 존재하지 않는다면 추가할 수 없습니다.");
            }
        }
    }

    @Nested
    @DisplayName("deleteSection 메서드는")
    class Describe_deleteSection {

        @Nested
        @DisplayName("상행 종점 지하철 역을 삭제할 경우")
        class Context_delete_UpEndStation {

            @Test
            @DisplayName("상행 종점 구간을 삭제한다.")
            void it_verify_delete_upEndSection() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                sectionService.deleteSection(lineId, upEndStation);
                // then
                verify(sectionDao).delete(upEndStation);
            }
        }

        @Nested
        @DisplayName("하행 종점 지하철 역을 삭제할 경우")
        class Context_delete_downEndStation {

            @Test
            @DisplayName("하행 종점 구간을 삭제한다.")
            void it_verify_delete_downEndSection() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                sectionService.deleteSection(lineId, downEndStation);
                // then
                verify(sectionDao).delete(2L);
            }
        }

        @Nested
        @DisplayName("노선 중간에 위치한 역을 삭제할 경우")
        class Context_delete_wayPointStation {

            @Test
            @DisplayName("해당 역이 포함된 노선을 삭제 후 재배치 한다.")
            void it_verify_delete_downEndSection() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                sectionService.deleteSection(lineId, middleStation);
                // then
                verify(sectionDao).delete(1L);
                verify(sectionDao).delete(2L);
                verify(sectionDao).save(new Section(lineId, upEndStation, downEndStation, 20));
            }
        }

        @Nested
        @DisplayName("노선에 등록된 단 하나의 구간을 삭제할 경우")
        class Context_delete_when_onlyOneSection {

            Sections onlyOneSection = new Sections(List.of(firstSection));

            @Test
            @DisplayName("예외가 발생한다.")
            void it_verify_delete_minimumSizeException() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(onlyOneSection);
                assertThatThrownBy(() -> sectionService.deleteSection(lineId, middleStation))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("[ERROR] 최소 하나 이상의 구간이 존재하여야합니다.");
            }

        }

        @Nested
        @DisplayName("노선에 등록된 구간에 존재하지 않는 지하철 역을 삭제할 경우")
        class Context_delete_noExistSection {

            Long noExistStationId = 5L;

            @Test
            @DisplayName("예외가 발생한다.")
            void it_verify_delete_noExistSectionException() {
                // given
                given(sectionDao.findByLineId(lineId)).willReturn(sections);
                assertThatThrownBy(() -> sectionService.deleteSection(lineId, noExistStationId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("[ERROR] 구간으로 등록되지 않은 지하철역 정보입니다.");
            }
        }
    }
}

