package wooteco.subway.service;

import javax.validation.Valid;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.SectionServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionServiceDto save(@Valid final SectionServiceDto dto) {
        Section section = new Section(dto.getLineId(), dto.getUpStationId(), dto.getDownStationId(), dto.getDistance());
        Sections sections = new Sections(sectionDao.findSectionsByLineId(section.getLineId()));
        sections.validateSavable(section);

        Section saveSection = sectionDao.save(section);

        return SectionServiceDto.from(saveSection);
    }

}

