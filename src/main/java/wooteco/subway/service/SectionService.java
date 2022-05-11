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
import wooteco.subway.domain.dto.AddSectionResult;
import wooteco.subway.domain.dto.RemoveStationResult;
import wooteco.subway.domain.entity.LineEntity;
import wooteco.subway.domain.entity.SectionEntity;
import wooteco.subway.dto.SectionInsertResponse;
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

    public SectionInsertResponse insertSection(Long lineId, SectionRequest sectionRequest) {
        Line line = convertLineEntityToLine(getLineEntity(lineId));
        List<SectionEntity> allSectionByLineId = sectionDao.findAllByLineId(lineId);
        Section section = convertSectionRequestToSection(lineId, sectionRequest);

        if (isFirstSection(allSectionByLineId)) {
            SectionEntity saveEntity = sectionDao.save(section);
            return new SectionInsertResponse(new SectionResponse(saveEntity));
        }

        AddSectionResult addSectionResult = line.addSection(section);
        return updateAfterInsert(addSectionResult);
    }

    private SectionInsertResponse updateAfterInsert(AddSectionResult addSectionResult) {
        if (addSectionResult.isNewEndSectionInserted()) {
            Section section = addSectionResult.getNewEndSection();
            SectionEntity saveEntity = sectionDao.save(section);
            return new SectionInsertResponse(new SectionResponse(saveEntity));
        }

        SectionEntity firstSplitSectionEntity = sectionDao.save(addSectionResult.getFirstSplitSection());
        SectionEntity secondSplitSectionEntity = sectionDao.save(addSectionResult.getSecondSplitSection());
        sectionDao.deleteById(addSectionResult.getRemovedSection().getId());

        return new SectionInsertResponse(
                List.of(new SectionResponse(firstSplitSectionEntity),
                        new SectionResponse(secondSplitSectionEntity)
                ),
                new SectionResponse(addSectionResult.getRemovedSection()));
    }

    public Station deleteSection(Long lineId, Long stationId) {
        Line line = convertLineEntityToLine(getLineEntity(lineId));
        Station station = getStation(stationId);
        RemoveStationResult removeStationResult = line.removeStation(station);
        updateAfterDelete(removeStationResult);
        return station;
    }

    private void updateAfterDelete(RemoveStationResult removeStationResult) {
        if (removeStationResult.isUpperEndSectionDeleted()) {
            sectionDao.deleteById(removeStationResult.getRemovedUpEndSection().getId());
            return;
        }

        if (removeStationResult.isDownEndSectionDeleted()) {
            sectionDao.deleteById(removeStationResult.getRemovedDownEndSection().getId());
            return;
        }

        sectionDao.deleteById(removeStationResult.getRemovedUpSection().getId());
        sectionDao.deleteById(removeStationResult.getRemovedDownSection().getId());
        sectionDao.save(removeStationResult.getMergedSection());
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
                .orElseThrow(() -> new StationNotFoundException(upStationId));
    }

    private LineEntity getLineEntity(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(lineId));
    }

    private boolean isFirstSection(List<SectionEntity> allSectionByLineId) {
        return allSectionByLineId.isEmpty();
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(stationId));
    }
}
