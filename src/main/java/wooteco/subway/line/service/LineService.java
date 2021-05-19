package wooteco.subway.line.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Color;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Name;
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
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.dto.StationServiceDto;

@Service
public class LineService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(final SectionDao sectionDao, final LineDao lineDao,
        final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public SectionServiceDto saveSectionByLineCreate(@Valid final SectionServiceDto sectionServiceDto) {
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
    public SectionServiceDto saveSection(@Valid final SectionServiceDto sectionServiceDto) {
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
        Station upStation = stationDao.show(sectionServiceDto.getUpStationId())
            .orElseThrow(()-> new NotFoundStationException());
        Station downStation = stationDao.show(sectionServiceDto.getDownStationId())
            .orElseThrow(() ->new NotFoundStationException());
        Line line = lineDao.show(sectionServiceDto.getLineId())
            .orElseThrow(()-> new NotFoundLineException());
        Distance distance = new Distance(sectionServiceDto.getDistance());
        return new Section(line, upStation, downStation, distance);
    }

    private void checkExistedStation(SectionServiceDto sectionServiceDto) {
        List<StationServiceDto> dtos = stationDao.showAll().stream()
            .map(station -> new StationServiceDto(station.getId(), station.getName()))
            .collect(Collectors.toList());
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
    public void deleteSection(@Valid final DeleteStationDto deleteDto) {
        Sections sections = new Sections(sectionDao.findAllByLineId(deleteDto.getLineId()));
        Station targetStation = stationDao.show(deleteDto.getStationId())
            .orElseThrow(() -> new NotFoundStationException());
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

    public List<StationResponse> findAllStationbyLindId(final Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.sortedStationIds()
            .stream()
            .map(this::stationResponseById)
            .collect(Collectors.toList());
    }

    private StationResponse stationResponseById(final Long id) {
        StationServiceDto dto = stationDao.showAll()
            .stream()
            .map(station -> new StationServiceDto(station.getId(), station.getName()))
            .filter(element -> id.equals(element.getId()))
            .findAny()
            .orElseThrow(NotFoundStationException::new);

        return new StationResponse(dto.getId(), dto.getName());
    }
    @Transactional
    public LineServiceDto createLine(@Valid final CreateLineDto createLineDto) {
        checkExistedName(new Name(createLineDto.getName()));
        checkExistedColor(new Color(createLineDto.getColor()));
        Line line = createLineDto.toLineEntity();
        Line saveLine = lineDao.save(line);
        SectionServiceDto sectionServiceDto = SectionServiceDto.of(saveLine, createLineDto);
        saveSectionByLineCreate(sectionServiceDto);
        return LineServiceDto.from(saveLine);
    }

    private void checkExistedColor(Color color) {
        if (lineDao.countByColor(color.value()) != 0) {
            throw new DuplicateLineException();
        }
    }

    private void checkExistedName(Name name) {
        if (lineDao.countByName(name.value()) != 0) {
            throw new DuplicateLineException();
        }
    }

    public LineWithComposedStationsDto findOne(@Valid final LineServiceDto lineServiceDto) {
        final Long lineId = lineServiceDto.getId();
        Line line = lineDao.show(lineId)
            .orElseThrow(() -> new NotFoundLineException());
        List<StationResponse> stationResponses = findAllStationbyLindId(line.getId());
        return LineWithComposedStationsDto.of(line, stationResponses);
    }

    public List<LineServiceDto> findAllDto() {
        return lineDao.showAll().stream()
            .map(line -> new LineServiceDto(line.getId(), line.getName(), line.getColor()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void delete(LineServiceDto lineServiceDto) {
        Long id = lineServiceDto.getId();
        if (lineDao.delete(lineServiceDto.getId()) == 0) {
            throw new NotFoundLineException();
        }
    }

    @Transactional
    public void update(LineServiceDto lineServiceDto) {
        Long id = lineServiceDto.getId();

        Line targetLine = lineDao.show(id)
            .orElseThrow(() -> new NotFoundLineException());
        Name updateName = new Name(lineServiceDto.getName());
        Color updatedColor = new Color(lineServiceDto.getColor());
        targetLine.update(updateName, updatedColor);

        if (lineDao.update(targetLine) == 0)
            throw new NotFoundLineException();
    }

    @Transactional
    public void createSection(CreateSectionDto createSectionDto) {
        SectionServiceDto sectionServiceDto = SectionServiceDto.from(createSectionDto);
        saveSection(sectionServiceDto);
    }

    @Transactional
    public void deleteStation(final long lineId, final long stationId) {
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationId);
        deleteSection(deleteStationDto);
    }
}

