package wooteco.subway.service;

import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.service.dto.SectionServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionServiceDto save(@Valid final SectionServiceDto dto) {
        Section section = new Section(dto.getLineId(), dto.getUpStationId(), dto.getDownStationId(),
            dto.getDistance());
        checkAvailableSaveSectionOnLine(section);
        Section saveSection = sectionDao.save(section);

        return SectionServiceDto.from(saveSection);
    }

    private void checkAvailableSaveSectionOnLine(final Section section) {
        List<Section> sections = sectionDao.findSectionsByLineId(section.getLineId());

        if (existedUpStation(section, sections) == existedDownStation(section, sections)) {
            throw new InvalidSectionOnLineException();
        }
    }

    private boolean existedUpStation(Section section, List<Section> sections) {
        return sections.stream()
            .map(Section::getUpStationId)
            .anyMatch(
                id -> id.equals(section.getUpStationId()) ^ id.equals(section.getDownStationId()));
    }

    private boolean existedDownStation(Section section, List<Section> sections) {
        return sections.stream()
            .map(Section::getDownStationId)
            .anyMatch(
                id -> id.equals(section.getUpStationId()) ^ id.equals(section.getDownStationId()));
    }
}

