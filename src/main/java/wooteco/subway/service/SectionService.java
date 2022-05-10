package wooteco.subway.service;

import java.util.ArrayList;
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
        updateChangedSections(sections);

        SectionEntity saveEntity = sectionDao.save(section);
        return new SectionResponse(saveEntity.getLineId(), saveEntity.getUpStationId(), saveEntity.getDownStationId(),
                saveEntity.getDistance());
    }

    private void updateChangedSections(List<Section> changedSections) {
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

    private Section convertSectionRequestToSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationDao.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        sectionRequest.getUpStationId()));

        Station downStation = stationDao.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        sectionRequest.getDownStationId()));

        return new Section(lineId, upStation, downStation,
                sectionRequest.getDistance());
    }

    private Line convertLineEntityToLine(LineEntity lineEntity) {
        Station upStation = stationDao.findById(lineEntity.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        lineEntity.getUpStationId()));

        Station downStation = stationDao.findById(lineEntity.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        lineEntity.getDownStationId()));

        List<Section> sections = sectionDao.findAllByLineId(lineEntity.getId()).stream()
                .map(this::convertSectionEntityToSection)
                .collect(Collectors.toList());

        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), upStation, downStation,
                lineEntity.getDistance(), new Sections(sections));
    }

    private Section convertSectionEntityToSection(SectionEntity sectionEntity) {
        Station upStation = stationDao.findById(sectionEntity.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        sectionEntity.getUpStationId()));

        Station downStation = stationDao.findById(sectionEntity.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(
                        sectionEntity.getDownStationId()));

        return new Section(sectionEntity.getLineId(), upStation, downStation,
                sectionEntity.getDistance());
    }
}
