package wooteco.subway.service;

import javax.validation.Valid;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.service.dto.DeleteStationDto;
import wooteco.subway.service.dto.SectionServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionServiceDto saveByLineCreate(@Valid final SectionServiceDto sectionServiceDto) {
        Section section = sectionServiceDto.toEntity();
        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));

        if (sections.isNotEmpty()) {
            throw new InvalidSectionOnLineException();
        }
        return saveSectionAtEnd(section);
    }

    public SectionServiceDto save(@Valid final SectionServiceDto sectionServiceDto) {
        Section section = sectionServiceDto.toEntity();
        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));
        sections.insertAvailable(section);

        if (sections.isBothEndSection(section)) {
            return saveSectionAtEnd(section);
        }
        return saveSectionAtMiddle(section, sections);
    }

    private SectionServiceDto saveSectionAtEnd(final Section section) {
        return SectionServiceDto.from(sectionDao.save(section));
    }

    private SectionServiceDto saveSectionAtMiddle(final Section section, final Sections sections) {
        Section legacySection = sections.findByStationId(section);
        sectionDao.save(legacySection.updateForSave(section));
        sectionDao.delete(legacySection);
        return SectionServiceDto.from(sectionDao.save(section));
    }

    public void delete(@Valid final DeleteStationDto deleteDto) {
        Sections sections = new Sections(sectionDao.findAllByLineId(deleteDto.getLineId()));
        sections.validateDeletableCount();
        sections.validateExistStation(deleteDto.getStationId());

        if (sections.isBothEndStation(deleteDto.getStationId())) {
            deleteStationAtEnd(deleteDto);
            return;
        }
        deleteStationAtMiddle(deleteDto);
    }

    private void deleteStationAtEnd(final DeleteStationDto dto) {
        if (sectionDao.findByLineIdAndUpStationId(dto.getLineId(), dto.getStationId()).isPresent()) {
            sectionDao.deleteByLineIdAndUpStationId(dto.getLineId(), dto.getStationId());
        }
        sectionDao.deleteByLineIdAndDownStationId(dto.getLineId(), dto.getStationId());
    }

    private void deleteStationAtMiddle(final DeleteStationDto dto) {
        Section upSection = sectionDao.findByLineIdAndDownStationId(dto.getLineId(), dto.getStationId())
                .orElseThrow(InvalidSectionOnLineException::new);
        Section downSection = sectionDao.findByLineIdAndUpStationId(dto.getLineId(), dto.getStationId())
                .orElseThrow(InvalidSectionOnLineException::new);

        Section updatedSection = upSection.updateForDelete(downSection);
        sectionDao.delete(upSection);
        sectionDao.delete(downSection);
        sectionDao.save(updatedSection);
    }
}

