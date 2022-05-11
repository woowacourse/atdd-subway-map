package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.entity.LineEntity;
import wooteco.subway.domain.entity.SectionEntity;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.utils.exceptions.LineNotFoundException;
import wooteco.subway.utils.exceptions.StationNotFoundException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public SectionResponse insertSection(Long lineId, SectionRequest sectionRequest) {
        LineEntity lineEntity = lineDao.findById(lineId).orElseThrow(() -> new LineNotFoundException(
                lineId));
        List<SectionEntity> allSectionByLineId = sectionDao.findAllByLineId(lineId);
        Line line = convertLineEntityToLine(lineEntity);
        Section section = convertSectionRequestToSection(lineId, sectionRequest);

        if (allSectionByLineId.isEmpty()) {
            SectionEntity saveEntity = sectionDao.save(section);
            return new SectionResponse(saveEntity.getLineId(), saveEntity.getUpStationId(), saveEntity.getDownStationId(),
                    saveEntity.getDistance());
        }

        List<Section> sections = line.addSection(section);
        updateAfterInsert(sections);

        SectionEntity saveEntity = sectionDao.save(section);
        return new SectionResponse(saveEntity.getLineId(), saveEntity.getUpStationId(), saveEntity.getDownStationId(),
                saveEntity.getDistance());
    }

    private void updateAfterInsert(List<Section> changedSections) {
        if (changedSections.size() == 1) {
            Section section = changedSections.get(0);
            sectionDao.save(section);
            return;
        }
        Section newFirstSection = changedSections.get(0);
        Section newSecondSection = changedSections.get(1);
        Section removedSection = changedSections.get(2);
        sectionDao.save(newFirstSection);
        sectionDao.save(newSecondSection);
        sectionDao.deleteById(removedSection.getId());
    }

    // TODO:
    public Station deleteSection(Long lineId, Long stationId) {
        LineEntity lineEntity = lineDao.findById(lineId).orElseThrow(() -> new LineNotFoundException(
                lineId));
        Line line = convertLineEntityToLine(lineEntity);
        Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(stationId));
        List<Section> sections = line.removeStation(station);
        updateAfterDelete(sections);
        return station;
    }

    private void updateAfterDelete(List<Section> sections) {
        if (sections.size() == 1) {
            Section deletedSection = sections.get(0);
            sectionDao.deleteById(deletedSection.getId());
            return;
        }
        Section upDeletedSection = sections.get(1);
        sectionDao.deleteById(upDeletedSection.getId());
        Section downDeletedSection = sections.get(2);
        sectionDao.deleteById(downDeletedSection.getId());
        Section mergedSection = sections.get(0);
        sectionDao.save(mergedSection);
    }

    private Section convertSectionRequestToSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = getStationFromId(sectionRequest.getUpStationId());
        Station downStation = getStationFromId(sectionRequest.getDownStationId());

        return new Section(lineId, upStation, downStation,
                sectionRequest.getDistance());
    }

    private Line convertLineEntityToLine(LineEntity lineEntity) {
        Station upStation = getStationFromId(lineEntity.getUpStationId());
        Station downStation = getStationFromId(lineEntity.getDownStationId());

        List<Section> sections = sectionDao.findAllByLineId(lineEntity.getId()).stream()
                .map(this::convertSectionEntityToSection)
                .collect(Collectors.toList());

        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), upStation, downStation,
                lineEntity.getDistance(), new Sections(sections));
    }

    private Section convertSectionEntityToSection(SectionEntity sectionEntity) {
        Station upStation = getStationFromId(sectionEntity.getUpStationId());
        Station downStation = getStationFromId(sectionEntity.getDownStationId());

        return new Section(sectionEntity.getLineId(), upStation, downStation,
                sectionEntity.getDistance());
    }

    private Station getStationFromId(Long upStationId) {
        return stationDao.findById(upStationId)
                .orElseThrow(() -> new StationNotFoundException(
                        upStationId));
    }
}
