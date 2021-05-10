package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.service.dto.SectionServiceDto;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    @Mock
    private SectionDao mockSectionDao;
    @InjectMocks
    private SectionService sectionService;

    @Test
    @DisplayName("기존 노선과 역이 존재하는 상태에서 구간 추가")
    void createSectionWithLineAndStations() {
        // given
        long sectionId = 1L;
        long lineId = 1L;
        long downStationId = 1L;
        long upStationId = 2L;
        int distance = 10;
        SectionServiceDto requestDto = new SectionServiceDto(lineId, upStationId, downStationId, distance);
        when(mockSectionDao.save(any(Section.class)))
            .thenReturn(new Section(sectionId, lineId, upStationId, downStationId, distance));

        // when
        SectionServiceDto sectionServiceDto = sectionService.save(requestDto);

        // then
        assertThat(sectionServiceDto.getId()).isNotNull();
        assertThat(sectionServiceDto.getLineId()).isEqualTo(requestDto.getLineId());
        assertThat(sectionServiceDto.getUpStationId()).isEqualTo(requestDto.getUpStationId());
        assertThat(sectionServiceDto.getDownStationId()).isEqualTo(requestDto.getDownStationId());
        assertThat(sectionServiceDto.getDistance()).isEqualTo(requestDto.getDistance());
    }
}
