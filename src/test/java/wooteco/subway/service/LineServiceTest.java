package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;


    @Test
    @DisplayName("중복된 이름을 저장한다.")
    void duplicatedNameException() {
        //given
        String name = "선릉역";
        String color = "red";
        //when
        given(lineDao.existByName(any(String.class))).willReturn(true);
        //then
        assertThatThrownBy(() -> lineService.saveLine(new LineRequest(name, color)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @Test
    @DisplayName("중복된 색을 저장한다.")
    void duplicatedColorException() {
        //given
        String name = "강남역";
        String color = "orange";
        //when
        given(lineDao.existByColor(any(String.class))).willReturn(true);
        //then
        assertThatThrownBy(() -> lineService.saveLine(new LineRequest(name, color)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @DisplayName("삭제할 lin e의 sections 크기가 1일 경우 예외를 발생시킨다.")
    @Test
    void SectionSizeException() {
        //given

        //when
        given(sectionDao.findByLineId(any(Long.class))).willReturn(List.of(new Section(1L, 1L, 2L, 1)));
        //then
        assertThatThrownBy(() -> lineService.deleteSectionByLineIdAndStationId(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 구간이 1개 이상은 존재해야합니다.");
    }

}