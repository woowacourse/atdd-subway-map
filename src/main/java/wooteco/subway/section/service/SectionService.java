package wooteco.subway.section.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;
import wooteco.subway.line.dto.CreateLineDto;
import wooteco.subway.line.dto.LineServiceDto;
import wooteco.subway.line.dto.LineWithComposedStationsDto;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.dto.CreateSectionDto;
import wooteco.subway.section.dto.DeleteStationDto;
import wooteco.subway.section.dto.SectionServiceDto;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.dto.StationServiceDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(final SectionDao sectionDao, final LineService lineService,
        final StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public SectionServiceDto saveByLineCreate(@Valid final SectionServiceDto sectionServiceDto) {
        Section section = assembleFromSectionServiceDto(sectionServiceDto);
        final Long lineId = section.getLine().getId();
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        checkExistedStation(sectionServiceDto);
        if (sections.isNotEmpty()) {
            throw new InvalidSectionOnLineException();
        }
        return saveSectionAtEnd(section);
    }

    @Transactional
    public SectionServiceDto save(@Valid final SectionServiceDto sectionServiceDto) {
        Section section = assembleFromSectionServiceDto(sectionServiceDto);
        final long lineId = sectionServiceDto.getLineId();
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.insertAvailable(section);

        if (sections.isBothEndSection(section)) {
            return saveSectionAtEnd(section);
        }
        return saveSectionAtMiddle(section, sections);
    }

    private Section assembleFromSectionServiceDto(SectionServiceDto sectionServiceDto) {
        Station upStation = stationService.showOne(sectionServiceDto.getUpStationId());
        Station downStation = stationService.showOne(sectionServiceDto.getDownStationId());
        Line line = lineService.show(sectionServiceDto.getLineId());
        Distance distance = new Distance(sectionServiceDto.getDistance());
        return new Section(line, upStation, downStation, distance);
    }

    private void checkExistedStation(SectionServiceDto sectionServiceDto) {
        List<StationServiceDto> dtos = stationService.showAllDto();
        Long upStationId = sectionServiceDto.getUpStationId();
        Long downStationId = sectionServiceDto.getDownStationId();
        checkOneStation(upStationId, dtos);
        checkOneStation(downStationId, dtos);
    }

    private void checkOneStation(Long stationId, List<StationServiceDto> dtos) {
        dtos.stream().map(StationServiceDto::getId)
            .filter(it -> it.equals(stationId))
            .findAny()
            .orElseThrow(NotFoundStationException::new);
    }

    private SectionServiceDto saveSectionAtEnd(final Section section) {
        return SectionServiceDto.from(sectionDao.save(section));
    }

    private SectionServiceDto saveSectionAtMiddle(final Section section, final Sections sections) {
        Section legacySection = sections.sectionForInterval(section);
        sectionDao.delete(legacySection);
        sectionDao.save(legacySection.dividedSectionForSave(section));
        return SectionServiceDto.from(sectionDao.save(section));
    }

    @Transactional
    public void delete(@Valid final DeleteStationDto deleteDto) {
        Sections sections = new Sections(sectionDao.findAllByLineId(deleteDto.getLineId()));
        Station targetStation = stationService.showOne(deleteDto.getStationId());
        sections.validateDeletableCount();
        sections.validateExistStation(targetStation);

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

        Section updatedSection = upSection.assembledSectionForDelete(downSection);
        sectionDao.delete(upSection);
        sectionDao.delete(downSection);
        sectionDao.save(updatedSection);
    }

    public List<StationResponse> findAllbyLindId(final Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.sortedStationIds()
            .stream()
            .map(this::stationResponseById)
            .collect(Collectors.toList());
    }

    private StationResponse stationResponseById(final Long id) {
        StationServiceDto dto = stationService.showAllDto()
            .stream()
            .filter(element -> id.equals(element.getId()))
            .findAny()
            .orElseThrow(NotFoundStationException::new);

        return new StationResponse(dto.getId(), dto.getName());
    }
    @Transactional
    public LineServiceDto createLine(@Valid final CreateLineDto createLineDto) {
        Line line = createLineDto.toLineEntity();
        Line saveLine = lineService.save(line);
        SectionServiceDto sectionServiceDto = SectionServiceDto.of(saveLine, createLineDto);
        saveByLineCreate(sectionServiceDto);
        return LineServiceDto.from(saveLine);
    }

    public LineWithComposedStationsDto findOne(@Valid final LineServiceDto lineServiceDto) {
        final Long lineId = lineServiceDto.getId();
        Line line =lineService.show(lineId);
        List<StationResponse> stationResponses = findAllbyLindId(line.getId());
        return LineWithComposedStationsDto.of(line, stationResponses);
    }

    public List<LineServiceDto> findAllLineDto() {
        return lineService.findAll();
    }

    @Transactional
    public void deleteLine(LineServiceDto lineServiceDto) {
        lineService.delete(lineServiceDto);
    }

    @Transactional
    public void updateLine(LineServiceDto lineServiceDto) {
        lineService.update(lineServiceDto);
    }

    @Transactional
    public void create(CreateSectionDto createSectionDto) {
        SectionServiceDto sectionServiceDto = SectionServiceDto.from(createSectionDto);
        save(sectionServiceDto);
    }

    @Transactional
    public void deleteStation(final long lineId, final long stationId) {
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationId);
        delete(deleteStationDto);
    }
}

