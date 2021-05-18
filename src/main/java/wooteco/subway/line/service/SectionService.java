package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, LineRequest lineRequest) {
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();

        duplicateInputStations(upStationId, downStationId);
        Section section = new Section(lineId, upStationId, downStationId, lineRequest.getDistance());
        sectionDao.save(section);
    }

    private void duplicateInputStations(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("중복된 지하철 역입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findSectionByLineId(Long id) {
        Sections sections = new Sections(sectionDao.findSectionBylineId(id));
        return sections.getOrderedStations().stream()
                .map(station ->
                        new StationResponse(
                                station.getId(),
                                station.getName()
                        ))
                .collect(Collectors.toList());
    }

    public void saveSectionOfExistLine(Long lineId, LineRequest lineRequest) {
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        duplicateInputStations(upStationId, downStationId);

        Sections sections = new Sections(sectionDao.findSectionBylineId(lineId));
        Station station = sections.findSameStationsOfSection(upStationId, downStationId);

        Section newSection = new Section(lineId, upStationId, downStationId, lineRequest.getDistance());
        updateNewStation(station, sections, newSection);
        sectionDao.save(newSection);
    }

    private void updateNewStation(Station station, Sections sections, Section newSection) {
        Long lineId = newSection.getLine().getId();
        Long upStationId = newSection.getUpStation().getId();
        Long downStationId = newSection.getDownStation().getId();
        int distance = newSection.getDistance();

        boolean isUpStation = station.isSame(upStationId);
        Section selectedSection = sections.findSelectedSection(isUpStation, newSection);
        if (isUpStation) {
            sectionDao.updateUpStation(lineId, upStationId, downStationId, selectedSection.getDistance() - distance);
            return;
        }
        sectionDao.updateDownStation(lineId, downStationId, upStationId, selectedSection.getDistance() - distance);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findSectionBylineId(lineId));
        sections.validDeletableSection();

        sectionDao.deleteByStationId(lineId, stationId);

        if (sections.notEndStation(stationId)) {
            Section sectionOfSameUpStation = sections.selectedSection(true, stationId);
            Section sectionOfSameDownStation = sections.selectedSection(false, stationId);

            sectionDao.save(lineId,
                    sectionOfSameDownStation.getUpStation().getId(),
                    sectionOfSameUpStation.getDownStation().getId(),
                    sectionOfSameUpStation.getDistance() + sectionOfSameDownStation.getDistance());
        }
    }

    public void deleteSectionByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }
}
