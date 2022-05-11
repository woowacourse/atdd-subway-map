package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
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

    public Line registerLine(final LineRequestDto lineRequestDto) {
        final Station upStation = stationService.searchById(lineRequestDto.getUpStationId());
        final Station downStation = stationService.searchById(lineRequestDto.getDownStationId());
        final List<Station> stations = List.of(upStation, downStation);
        final Line line = new Line(lineRequestDto.getName(), lineRequestDto.getColor(), stations);
        try {
            final LineEntity savedLineEntity = lineDao.save(new LineEntity(line));
            final Section section = new Section(upStation, downStation, lineRequestDto.getDistance());
            sectionDao.save(new SectionEntity(savedLineEntity.getId(), section));
            return new Line(savedLineEntity.getId(), savedLineEntity.getName(), savedLineEntity.getColor(), stations);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateLineNameException();
        }
    }

    public Line searchLineById(final Long id) {
        final LineEntity lineEntity = lineDao.findById(id);
        final List<Station> stations = new SectionsOnTheLine(searchSectionsByLineId(id)).lineUpStations();
        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), stations);
    }

    public List<Line> searchAllLines() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> {
                    final Long id = lineEntity.getId();
                    final List<Station> stations = new SectionsOnTheLine(searchSectionsByLineId(id)).lineUpStations();
                    return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), stations);
                }).collect(Collectors.toList());
    }

    public void modifyLine(final Long id, final LineRequestDto lineRequestDto) {
        lineDao.update(new LineEntity(id, lineRequestDto.getName(), lineRequestDto.getColor()));
    }

    public void removeLine(final Long id) {
        lineDao.deleteById(id);
    }

    public void registerSection(final Long lineId, final SectionRequestDto sectionRequestDto) {
        final Line line = searchLineById(lineId);
        final Station upStation = stationService.searchById(sectionRequestDto.getUpStationId());
        final Station downStation = stationService.searchById(sectionRequestDto.getDownStationId());
        final Section section = new Section(upStation, downStation, sectionRequestDto.getDistance());
        final SectionsOnTheLine sectionsOnTheLine = new SectionsOnTheLine(searchSectionsByLineId(line.getId()));
        if (sectionsOnTheLine.isAddableOnTheLine(section)) {
            final Section overlapSection = sectionsOnTheLine.findOverlapSection(section);
            registerSectionWhenOnTheLine(line.getId(), section, overlapSection);
            return;
        }
        sectionDao.save(new SectionEntity(lineId, section));
    }

    private void registerSectionWhenOnTheLine(final Long lineId, final Section section, final Section overlapSection) {
        if (overlapSection.isUpStationMatch(section.getUpStation())) {
            final SectionEntity sectionEntity = new SectionEntity(
                    overlapSection.getId(),
                    lineId,
                    section.getDownStation().getId(),
                    overlapSection.getDownStation().getId(),
                    overlapSection.getDistance() - section.getDistance());
            sectionDao.update(sectionEntity);
            sectionDao.save(new SectionEntity(lineId, section));
            return;
        }
        final SectionEntity sectionEntity = new SectionEntity(
                overlapSection.getId(),
                lineId,
                overlapSection.getUpStation().getId(),
                section.getUpStation().getId(),
                overlapSection.getDistance() - section.getDistance());
        sectionDao.update(sectionEntity);
        sectionDao.save(new SectionEntity(lineId, section));
        return;
    }

    public List<Section> searchSectionsByLineId(final Long lineId) {
        return sectionDao.findByLineId(lineId).stream()
                .map(sectionEntity -> new Section(
                        sectionEntity.getId(),
                        stationService.searchById(sectionEntity.getUpStationId()),
                        stationService.searchById(sectionEntity.getDownStationId()),
                        sectionEntity.getDistance()
                )).collect(Collectors.toList());
    }

    public void removeSection(final Long lineId, final Long stationId) {
        final Line line = searchLineById(lineId);
        final Station station = stationService.searchById(stationId);
        final SectionsOnTheLine sectionsOnTheLine = new SectionsOnTheLine(searchSectionsByLineId(line.getId()));
        validateRemoveSection(station, sectionsOnTheLine);
        if (sectionsOnTheLine.isTerminus(station)) {
            sectionDao.deleteByLineIdAndStationId(line.getId(), station.getId());
            return;
        }
        Section upperSection = sectionsOnTheLine.findByDownStation(station);
        Section lowerSection = sectionsOnTheLine.findByUpStation(station);
        Section section = new Section(
                upperSection.getUpStation(),
                lowerSection.getDownStation(),
                upperSection.getDistance() + lowerSection.getDistance()
        );
        sectionDao.deleteByLineIdAndStationId(line.getId(), station.getId());
        sectionDao.save(new SectionEntity(line.getId(), section));
    }

    private void validateRemoveSection(final Station station, final SectionsOnTheLine sectionsOnTheLine) {
        if (!sectionsOnTheLine.contains(station) || sectionsOnTheLine.hasSingleSection()) {
            throw new CanNotDeleteException();
        }
    }
}
