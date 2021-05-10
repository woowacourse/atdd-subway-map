package wooteco.subway.service;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.service.dto.SectionServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionServiceDto save(@Valid final SectionServiceDto sectionServiceDto) {
        Section section = sectionServiceDto.toEntity();
        validateSavable(section);

        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));

        if (sections.isBothEnd(section)) {
            return saveSectionAtEnd(section);
        }
        return saveSectionAtMiddle(section);
    }

    private void validateSavable(Section section) {
        boolean existUpStation = sectionDao
            .findByLineIdAndUpStationId(section.getLineId(), section.getUpStationId()).isPresent();
        boolean existDownStation = sectionDao
            .findByLineIdAndDownStationId(section.getLineId(), section.getDownStationId())
            .isPresent();

        if (existUpStation == existDownStation) {
            throw new InvalidSectionOnLineException();
        }
    }

    private SectionServiceDto saveSectionAtEnd(final Section section) {
        return SectionServiceDto.from(sectionDao.save(section));
    }

    private SectionServiceDto saveSectionAtMiddle(final Section section) {
        Optional<Section> sectionByUpStation = sectionDao.findByLineIdAndUpStationId(section.getLineId(), section.getUpStationId());
        Optional<Section> sectionByDownStation = sectionDao.findByLineIdAndDownStationId(section.getLineId(), section.getDownStationId());
        Section legacySection = sectionByUpStation.orElse(sectionByDownStation.orElseThrow(InvalidSectionOnLineException::new));

        Section updateSection = section.distanceUpdateSection(legacySection);
        sectionDao.updateStationAndDistance(updateSection);
        return SectionServiceDto.from(sectionDao.save(section));
    }
}

