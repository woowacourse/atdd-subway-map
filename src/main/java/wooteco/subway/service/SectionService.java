package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.InvalidSectionOnLineException;
import wooteco.subway.service.dto.SectionServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public SectionServiceDto saveByLineCreate(Line line, @Valid SectionServiceDto dto) {
        Section section = newSection(line, dto);
        Sections sections = new Sections(sectionDao.findAllByLine(section.getLine()));

        if (sections.isNotEmpty()) {
            throw new InvalidSectionOnLineException();
        }
        return saveSectionAtEnd(section);
    }

    public SectionServiceDto save(Line line, @Valid SectionServiceDto dto) {
        Section section = newSection(line, dto);
        Sections sections = new Sections(sectionDao.findAllByLine(section.getLine()));
        sections.validateInsertable(section);

        if (sections.isBothEndSection(section)) {
            return saveSectionAtEnd(section);
        }
        return saveSectionAtMiddle(section, sections);
    }

    private Section newSection(Line line, @Valid SectionServiceDto dto) {
        Station upStation = stationService.findById(dto.getUpStationId());
        Station downStation = stationService.findById(dto.getDownStationId());
        Distance distance = new Distance(dto.getDistance());
        return new Section(line, upStation, downStation, distance);
    }

    private SectionServiceDto saveSectionAtEnd(Section section) {
        return SectionServiceDto.from(sectionDao.save(section));
    }

    private SectionServiceDto saveSectionAtMiddle(Section section, Sections sections) {
        Section legacySection = sections.findByMatchStation(section);
        sectionDao.delete(legacySection);
        sectionDao.save(legacySection.updateForSave(section));
        return SectionServiceDto.from(sectionDao.save(section));
    }

    public void delete(Line line, @NotNull Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLine(line));
        Station station = stationService.findById(stationId);
        sections.validateDeletableCount();
        sections.validateExistStation(station);

        if (sections.isBothEndStation(station)) {
            deleteStationAtEnd(line, station);
            return;
        }
        deleteStationAtMiddle(line, station);
    }

    private void deleteStationAtEnd(Line line, Station station) {
        if (sectionDao.findByLineAndUpStation(line, station).isPresent()) {
            sectionDao.deleteByLineAndUpStation(line, station);
        }
        sectionDao.deleteByLineAndDownStation(line, station);
    }

    private void deleteStationAtMiddle(Line line, Station station) {
        Section upSection = sectionDao.findByLineAndDownStation(line, station)
            .orElseThrow(InvalidSectionOnLineException::new);
        Section downSection = sectionDao.findByLineAndUpStation(line, station)
            .orElseThrow(InvalidSectionOnLineException::new);

        Section updatedSection = upSection.updateForDelete(downSection);
        sectionDao.delete(upSection);
        sectionDao.delete(downSection);
        sectionDao.save(updatedSection);
    }

    public List<StationResponse> findAllByLind(Line line) {
        Sections sections = new Sections(sectionDao.findAllByLine(line));

        return sections.sortedStations()
            .stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
    }
}

