package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.service.dto.SectionServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionServiceDto save(final SectionServiceDto dto) {
        Section section = new Section(dto.getLineId(), dto.getUpStationId(), dto.getDownStationId(), dto.getDistance());
        Section saveSection = sectionDao.save(section);

        return SectionServiceDto.from(saveSection);
    }
}

