package wooteco.subway.service;

import java.util.Optional;
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

    public SectionServiceDto save(@Valid final SectionServiceDto sectionServiceDto) {
        Section section = sectionServiceDto.toEntity();
        validateSavable(section);

        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));

        if (sections.isBothEndSection(section)) {
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
        Optional<Section> sectionByUpStation = sectionDao
            .findByLineIdAndUpStationId(section.getLineId(), section.getUpStationId());
        Optional<Section> sectionByDownStation = sectionDao
            .findByLineIdAndDownStationId(section.getLineId(), section.getDownStationId());
        Section legacySection = sectionByUpStation
            .orElse(sectionByDownStation.orElseThrow(InvalidSectionOnLineException::new));

        Section updatedSection = section.updateForSave(legacySection);
        sectionDao.updateStationAndDistance(updatedSection);
        return SectionServiceDto.from(sectionDao.save(section));
    }

    public void delete(@Valid final DeleteStationDto deleteDto) {
        Sections sections = new Sections(sectionDao.findAllByLineId(deleteDto.getLineId()));
        validateDeletable(sections, deleteDto);

        if (sections.isBothEndStation(deleteDto.getStationId())) {
            deleteStationAtEnd(deleteDto);
        }
        deleteStationAtMiddle(deleteDto);
    }

    private void validateDeletable(final Sections sections, final DeleteStationDto deleteDto) {
        sections.validateDeletableCount();
        sections.validateExistStation(deleteDto.getStationId());
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
        sectionDao.save(updatedSection);
        sectionDao.delete(upSection);
        sectionDao.delete(downSection);
    }
}

