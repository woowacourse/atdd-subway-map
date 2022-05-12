package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionRepository;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.dao.entity.EntityAssembler;
import wooteco.subway.repository.dao.entity.line.LineEntity;
import wooteco.subway.repository.dao.entity.section.SectionEntity;
import wooteco.subway.repository.dao.entity.station.StationEntity;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.repository.exception.DuplicateStationNameException;
import wooteco.subway.repository.exception.NoSuchLineException;
import wooteco.subway.repository.exception.NoSuchSectionException;
import wooteco.subway.repository.exception.NoSuchStationException;

@Repository
public class SubwayRepository implements LineRepository, SectionRepository, StationRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SubwayRepository(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Override
    public Line saveLine(Line line) {
        Long lineId = saveLine(EntityAssembler.lineEntity(line));
        saveSections(lineId, line);
        return findLineById(lineId);
    }

    private Long saveLine(LineEntity lineEntity) {
        validateLineBeforeSave(lineEntity);
        return lineDao.save(lineEntity);
    }

    private void validateLineBeforeSave(LineEntity lineEntity) {
        validateLineNameNotDuplicated(lineEntity.getName());
        validateLineColorNotDuplicated(lineEntity.getColor());
    }

    private void validateLineNameNotDuplicated(String name) {
        if (lineDao.existsByName(name)) {
            throw new DuplicateLineNameException(name);
        }
    }

    private void validateLineColorNotDuplicated(String color) {
        if (lineDao.existsByColor(color)) {
            throw new DuplicateLineColorException(color);
        }
    }

    private void saveSections(Long lineId, Line line) {
        validateSectionsBeforeSave(lineId, line.getSections());
        List<SectionEntity> sectionEntities = EntityAssembler.sectionEntities(lineId, line);
        sectionEntities.forEach(sectionDao::save);
    }

    private void validateSectionsBeforeSave(Long lineId, List<Section> sections) {
        validateLineExist(lineId);
        validateStationsExist(sections);
    }

    private void validateStationsExist(List<Section> sections) {
        sections.forEach(section -> {
            validateStationExist(section.getUpStation().getId());
            validateStationExist(section.getDownStation().getId());
        });
    }

    @Override
    public List<Line> findLines() {
        return lineDao.findAll()
                .stream()
                .map(lineEntity -> EntityAssembler.line(lineEntity, findSectionsByLineId(lineEntity.getId())))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Line findLineById(Long lineId) {
        return lineDao.findById(lineId)
                .map(lineEntity -> EntityAssembler.line(lineEntity, findSectionsByLineId(lineEntity.getId())))
                .orElseThrow(() -> new NoSuchLineException(lineId));
    }

    @Override
    public List<Section> findSectionsByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId)
                .stream()
                .map(sectionEntity -> EntityAssembler.section(
                        findStationById(sectionEntity.getUpStationId()),
                        findStationById(sectionEntity.getDownStationId()),
                        sectionEntity))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Station findStationById(Long stationId) {
        return stationDao.findById(stationId)
                .map(EntityAssembler::station)
                .orElseThrow(() -> new NoSuchStationException(stationId));
    }

    @Override
    public Line updateLine(Line line) {
        validateLineExist(line.getId());
        lineDao.update(EntityAssembler.lineEntity(line));
        return findLineById(line.getId());
    }

    @Override
    public void updateSections(Long lineId, Sections sections) {
        List<Long> actualSectionIds = sectionDao.findAllIdByLineId(lineId);
        List<Long> expectedSectionIds = sections.getSectionIds();

        List<Long> sectionIdsForRemove = actualSectionIds.stream()
                .filter(sectionId -> !expectedSectionIds.contains(sectionId))
                .collect(Collectors.toUnmodifiableList());

        List<Section> sectionsForAppend = sections.getSections()
                .stream()
                .filter(section -> section.getId() == 0)
                .collect(Collectors.toUnmodifiableList());

        sectionIdsForRemove.forEach(sectionDao::remove);
        sectionsForAppend.forEach(section -> sectionDao.save(EntityAssembler.sectionEntity(lineId, section)));
    }

    @Override
    public void removeLine(Long lineId) {
        Line line = findLineById(lineId);
        removeSections(line.getSectionIds());
        lineDao.remove(lineId);
    }

    private void removeSections(List<Long> sectionIds) {
        sectionIds.forEach(this::removeSection);
    }

    private void removeSection(Long sectionId) {
        validateSectionExist(sectionId);
        sectionDao.remove(sectionId);
    }

    @Override
    public Station saveStation(Station station) {
        Long stationId = saveStation(EntityAssembler.stationEntity(station));
        return findStationById(stationId);
    }

    private Long saveStation(StationEntity stationEntity) {
        validateStationBeforeSave(stationEntity);
        return stationDao.save(stationEntity);
    }

    private void validateStationBeforeSave(StationEntity stationEntity) {
        validateStationNameNotDuplicated(stationEntity.getName());
    }

    private void validateStationNameNotDuplicated(String name) {
        if (stationDao.existsByName(name)) {
            throw new DuplicateStationNameException(name);
        }
    }

    @Override
    public List<Station> findStations() {
        return stationDao.findAll()
                .stream()
                .map(EntityAssembler::station)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void removeStation(Long stationId) {
        Station station = findStationById(stationId);
        validateSectionsNotExistByStationId(station);
        stationDao.remove(station.getId());
    }

    private void validateSectionsNotExistByStationId(Station station) {
        if (sectionDao.existsByStationId(station.getId())) {
            throw new IllegalStateException(
                    String.format("지하철역을 구간으로 지니고 있는 지하철노선이 존재합니다. [지하철역 : %s]", station.getName()));
        }
    }

    private void validateLineExist(Long lineId) {
        if (!lineDao.existsById(lineId)) {
            throw new NoSuchLineException(lineId);
        }
    }

    private void validateSectionExist(Long sectionId) {
        if (!sectionDao.existsById(sectionId)) {
            throw new NoSuchSectionException(sectionId);
        }
    }

    private void validateStationExist(Long stationId) {
        if (!stationDao.existsById(stationId)) {
            throw new NoSuchStationException(stationId);
        }
    }
}
