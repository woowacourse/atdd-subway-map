package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequestDto;
import wooteco.subway.repository.dao.JdbcSectionDao;
import wooteco.subway.repository.entity.SectionEntity;

@Service
public class SectionService {

    // 인터페이스 SectionDao 로 바꿔야 함
    private final JdbcSectionDao sectionDao;

    private final StationService stationService;
    private final LineService lineService;

    public SectionService(final JdbcSectionDao sectionDao, final StationService stationService,
                          final LineService lineService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public void registerWhenRegisterLine(final Line line, final SectionRequestDto sectionRequestDto) {
        final Station upStation = stationService.searchById(sectionRequestDto.getUpStationId());
        final Station downStation = stationService.searchById(sectionRequestDto.getDownStationId());
        final Section section = new Section(line, upStation, downStation, sectionRequestDto.getDistance());
        sectionDao.save(new SectionEntity(section));
    }
}
