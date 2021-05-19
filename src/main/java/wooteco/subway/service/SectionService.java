package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.controller.dto.SectionRequest;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Transactional
@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, LineService lineService, StationService stationService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public Section save(Long lineId, SectionRequest sectionRequest) {
        final Station upStation = stationService.findById(sectionRequest.getUpStationId());
        final Station downStation = stationService.findById(sectionRequest.getDownStationId());
        final Section section = new Section(upStation, downStation, sectionRequest.getDistance());
        final Line line = lineService.findById(lineId).insertSection(section);

        if (line.shouldInsertAtSide(section)) {
            return sectionDao.save(lineId, section);
        }

        return insertAtMiddle(line, section);
    }

    private Section insertAtMiddle(Line line, Section section) {
        updateSection(line, section);
        return sectionDao.save(line.getId(), section);
    }

    private void updateSection(Line line, Section section) {
        if (line.shouldInsertAtUpStationOfMiddle(section)) {
            sectionDao.updateByLineIdAndDownStationId(line.getId(), line.findSectionByUpStation(section.getUpStation()));
            return;
        }
        sectionDao.updateByLineIdAndUpStationId(line.getId(), line.findSectionByUpStation(section.getUpStation()));
    }

    public void deleteSection(Long lineId, Long stationId) {
        final Line line = lineService.findById(lineId);
        final Station deleteStation = stationService.findById(stationId);
        if (line.isTopStation(deleteStation)) {
            deleteTopStation(lineId, stationId);
            return;
        }

        if (line.isBottomStation(deleteStation)) {
            deleteBottomStation(lineId, stationId);
            return;
        }

        deleteMiddleStation(line, deleteStation);
    }

    private void deleteTopStation(Long lineId, Long stationId) {
        sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
    }

    private void deleteBottomStation(Long lineId, Long stationId) {
        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
    }

    private void deleteMiddleStation(Line line, Station station) {
        final Long lineId = line.getId();
        sectionDao.deleteAllByLineId(lineId);
        sectionDao.saveAll(lineId, line.deleteStation(station));
    }
}
