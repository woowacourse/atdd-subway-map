package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionsOnTheLine;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequestDto;
import wooteco.subway.dto.request.SectionRequestDto;
import wooteco.subway.exception.CanNotDeleteException;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    private final StationService stationService;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void registerSection(final Long lineId, final SectionRequestDto sectionRequestDto) {
        final Line line = searchLineById(lineId);
        final Section section = creatSectionOf(sectionRequestDto);
        final SectionsOnTheLine sectionsOnTheLine = new SectionsOnTheLine(searchSectionsByLineId(line.getId()));
        if (sectionsOnTheLine.isAddableOnTheLine(section)) {
            saveSectionWhenOnTheLine(line.getId(), section, sectionsOnTheLine.findOverlapSection(section));
        }
        if (sectionsOnTheLine.isAddableOutOfLine(section)) {
            sectionDao.save(new SectionEntity(line.getId(), section));
        }
    }

    private Section creatSectionOf(final SectionRequestDto sectionRequestDto) {
        final Station upStation = stationService.searchById(sectionRequestDto.getUpStationId());
        final Station downStation = stationService.searchById(sectionRequestDto.getDownStationId());
        return Section.ofNullId(upStation, downStation, sectionRequestDto.getDistance());
    }

    private void saveSectionWhenOnTheLine(final Long lineId, final Section section, final Section overlapSection) {
        if (overlapSection.isUpStationMatch(section.getUpStation())) {
            updateOverlapSectionWhenUpStationMatch(lineId, section, overlapSection);
            sectionDao.save(new SectionEntity(lineId, section));
        }
        if (overlapSection.isDownStationMatch(section.getDownStation())) {
            updateOverlapSectionWhenDownStationMatch(lineId, section, overlapSection);
            sectionDao.save(new SectionEntity(lineId, section));
        }
    }

    private void updateOverlapSectionWhenUpStationMatch(final Long lineId,
                                                        final Section section,
                                                        final Section overlapSection) {
        final SectionEntity sectionEntity = new SectionEntity(
                overlapSection.getId(),
                lineId,
                section.getDownStation().getId(),
                overlapSection.getDownStation().getId(),
                overlapSection.getDistance() - section.getDistance());
        sectionDao.update(sectionEntity);
    }

    private void updateOverlapSectionWhenDownStationMatch(final Long lineId,
                                                          final Section section,
                                                          final Section overlapSection) {
        final SectionEntity sectionEntity = new SectionEntity(
                overlapSection.getId(),
                lineId,
                overlapSection.getUpStation().getId(),
                section.getUpStation().getId(),
                overlapSection.getDistance() - section.getDistance());
        sectionDao.update(sectionEntity);
    }

    public List<Section> searchSectionsByLineId(final Long lineId) {
        return sectionDao.findByLineId(lineId).stream()
                .map(sectionEntity -> createSectionOf(sectionEntity))
                .collect(Collectors.toList());
    }

    private Section createSectionOf(final SectionEntity sectionEntity) {
        final Station upStation = stationService.searchById(sectionEntity.getUpStationId());
        final Station downStation = stationService.searchById(sectionEntity.getDownStationId());
        return sectionEntity.createSection(upStation, downStation);
    }

    public void removeSection(final Long lineId, final Long stationId) {
        final Line line = searchLineById(lineId);
        final Station station = stationService.searchById(stationId);
        final SectionsOnTheLine sectionsOnTheLine = new SectionsOnTheLine(searchSectionsByLineId(line.getId()));
        validateStationForDeleteSection(station, sectionsOnTheLine);
        sectionDao.deleteByLineIdAndStationId(line.getId(), station.getId());
        if (!sectionsOnTheLine.isTerminus(station)) {
            saveSectionWhenDeleteStationOnTheLine(line.getId(), station, sectionsOnTheLine);
        }
    }

    private void validateStationForDeleteSection(final Station station, final SectionsOnTheLine sectionsOnTheLine) {
        if (!sectionsOnTheLine.contains(station) || sectionsOnTheLine.hasSingleSection()) {
            throw new CanNotDeleteException();
        }
    }

    private void saveSectionWhenDeleteStationOnTheLine(final Long lineId,
                                                       final Station station,
                                                       final SectionsOnTheLine sectionsOnTheLine) {
        final Section upperSection = sectionsOnTheLine.findByDownStation(station);
        final Section lowerSection = sectionsOnTheLine.findByUpStation(station);
        final Section section = Section.ofNullId(
                upperSection.getUpStation(),
                lowerSection.getDownStation(),
                upperSection.getDistance() + lowerSection.getDistance()
        );
        sectionDao.save(new SectionEntity(lineId, section));
    }

    public Line registerLine(final LineRequestDto lineRequestDto) {
        final Line line = createLineOf(lineRequestDto);
        try {
            final LineEntity savedLineEntity = lineDao.save(new LineEntity(line));
            saveSectionWhenSaveLine(savedLineEntity.getId(), lineRequestDto);
            final SectionsOnTheLine sectionsOnTheLine =
                    new SectionsOnTheLine(searchSectionsByLineId(savedLineEntity.getId()));
            return savedLineEntity.createLine(sectionsOnTheLine);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineNameException();
        }
    }

    private Line createLineOf(final LineRequestDto lineRequestDto) {
        final Station upStation = stationService.searchById(lineRequestDto.getUpStationId());
        final Station downStation = stationService.searchById(lineRequestDto.getDownStationId());
        final Section section = Section.ofNullId(upStation, downStation, lineRequestDto.getDistance());
        final SectionsOnTheLine sectionsOnTheLine = new SectionsOnTheLine(List.of(section));
        return Line.ofNullId(lineRequestDto.getName(), lineRequestDto.getColor(), sectionsOnTheLine);
    }

    private void saveSectionWhenSaveLine(final Long lineId, final LineRequestDto lineRequestDto) {
        final Station upStation = stationService.searchById(lineRequestDto.getUpStationId());
        final Station downStation = stationService.searchById(lineRequestDto.getDownStationId());
        final Section section = Section.ofNullId(upStation, downStation, lineRequestDto.getDistance());
        sectionDao.save(new SectionEntity(lineId, section));
    }

    public Line searchLineById(final Long id) {
        try {
            final LineEntity lineEntity = lineDao.findById(id);
            final SectionsOnTheLine sectionsOnTheLine = new SectionsOnTheLine(searchSectionsByLineId(id));
            return lineEntity.createLine(sectionsOnTheLine);
        } catch (EmptyResultDataAccessException exception) {
            throw new NoSuchElementException("[ERROR] 노선을 찾을 수 없습니다.");
        }
    }

    public List<Line> searchAllLines() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> {
                    final Long id = lineEntity.getId();
                    final SectionsOnTheLine sectionsOnTheLine = new SectionsOnTheLine(searchSectionsByLineId(id));
                    return lineEntity.createLine(sectionsOnTheLine);
                }).collect(Collectors.toList());
    }

    public void modifyLine(final Long id, final LineRequestDto lineRequestDto) {
        lineDao.update(new LineEntity(id, lineRequestDto.getName(), lineRequestDto.getColor()));
    }

    public void removeLine(final Long id) {
        lineDao.deleteById(id);
    }
}
