package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;


    @Test
    @DisplayName("중복된 이름을 저장한다.")
    void duplicatedNameException() {
        //given
        String name = "선릉역";
        String color = "red";
        //when
        given(lineDao.existByName(any(String.class))).willReturn(true);
        //then
        assertThatThrownBy(() -> lineService.save(new LineRequest(name, color)))
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
        assertThatThrownBy(() -> lineService.save(new LineRequest(name, color)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 Id 를 조회한다.")
    void findByIdException() {
        //given
        Long id = -1L;
        //when

        //then
        assertThatThrownBy(() -> lineService.findById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID의 노선은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("line 과 section 을 저장한다.")
    void save() {
        //given
        given(lineDao.save(any(Line.class))).willReturn(1L);
        //when
        LineResponse response = lineService.save(new LineRequest("2호선", "red", 1L, 2L, 10));
        //then

    }

}