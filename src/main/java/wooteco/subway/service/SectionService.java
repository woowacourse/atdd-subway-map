package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.exception.InvalidSectionOnLineException;
import wooteco.subway.service.dto.DeleteStationDto;
import wooteco.subway.service.dto.SectionServiceDto;
import wooteco.subway.service.dto.StationServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public SectionServiceDto saveByLineCreate(@Valid SectionServiceDto sectionServiceDto) {
        Section section = sectionServiceDto.toEntity();
        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));
        checkExistedStation(sectionServiceDto);
        if (sections.isNotEmpty()) {
            throw new InvalidSectionOnLineException();
        }
        return saveSectionAtEnd(section);
    }

    public SectionServiceDto save(@Valid SectionServiceDto sectionServiceDto) {
        Section section = sectionServiceDto.toEntity();
        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));
        sections.insertAvailable(section);

        if (sections.isBothEndSection(section)) {
            return saveSectionAtEnd(section);
        }
        return saveSectionAtMiddle(section, sections);
    }

    private void checkExistedStation(SectionServiceDto sectionServiceDto) {
        List<StationServiceDto> dtos = stationService.showStations();
        Long upStationId = sectionServiceDto.getUpStationId();
        Long downStationId = sectionServiceDto.getDownStationId();
        checkOneStation(upStationId, dtos);
        checkOneStation(downStationId, dtos);
    }

    private void checkOneStation(Long stationId, List<StationServiceDto> dtos) {
        dtos.stream().map(StationServiceDto::getId)
            .filter(it -> it.equals(stationId))
            .findAny()
            .orElseThrow(NotFoundException::new);
    }

    private SectionServiceDto saveSectionAtEnd(Section section) {
        return SectionServiceDto.from(sectionDao.save(section));
    }

    private SectionServiceDto saveSectionAtMiddle(Section section, Sections sections) {
        Section legacySection = sections.findByStationId(section);
        sectionDao.save(legacySection.updateForSave(section));
        sectionDao.delete(legacySection);
        return SectionServiceDto.from(sectionDao.save(section));
    }

    public void delete(@Valid DeleteStationDto deleteDto) {
        Sections sections = new Sections(sectionDao.findAllByLineId(deleteDto.getLineId()));
        sections.validateDeletableCount();
        sections.validateExistStation(deleteDto.getStationId());

        if (sections.isBothEndStation(deleteDto.getStationId())) {
            deleteStationAtEnd(deleteDto);
            return;
        }
        deleteStationAtMiddle(deleteDto);
    }

    private void deleteStationAtEnd(DeleteStationDto dto) {
        if (sectionDao.findByLineIdAndUpStationId(dto.getLineId(), dto.getStationId()).isPresent()) {
            sectionDao.deleteByLineIdAndUpStationId(dto.getLineId(), dto.getStationId());
        }
        sectionDao.deleteByLineIdAndDownStationId(dto.getLineId(), dto.getStationId());
    }

    private void deleteStationAtMiddle(DeleteStationDto dto) {
        Section upSection = sectionDao.findByLineIdAndDownStationId(dto.getLineId(), dto.getStationId())
                .orElseThrow(InvalidSectionOnLineException::new);
        Section downSection = sectionDao.findByLineIdAndUpStationId(dto.getLineId(), dto.getStationId())
                .orElseThrow(InvalidSectionOnLineException::new);

        Section updatedSection = upSection.updateForDelete(downSection);
        sectionDao.delete(upSection);
        sectionDao.delete(downSection);
        sectionDao.save(updatedSection);
    }

    public List<StationResponse> findAllbyLindId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.sortedStationIds()
            .stream()
            .map(this::stationReponseById)
            .collect(Collectors.toList());
    }

    private StationResponse stationReponseById(Long id) {
        StationServiceDto dto = stationService.showStations()
            .stream()
            .filter(element -> id.equals(element.getId()))
            .findAny()
            .orElseThrow(NotFoundException::new);

        return new StationResponse(dto.getId(), dto.getName());
    }
}

