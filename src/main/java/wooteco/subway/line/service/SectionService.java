package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.domain.strategy.DownWardStrategy;
import wooteco.subway.line.domain.strategy.Strategy;
import wooteco.subway.line.domain.strategy.UpWardStrategy;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
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

    @Transactional
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
        Long upStationId = newSection.getUpStation().getId();
        Strategy strategy = findStrategy(station.isSame(upStationId));
        Section section = strategy.selectedSection(sections, newSection);

        if (section.isLessOrSameDistance(newSection.getDistance())) {
            throw new IllegalArgumentException("거리가 현재 존재하는 구간보다 크거나 같습니다!");
        }

        strategy.updateStation(sectionDao, section, newSection);
    }

    private Strategy findStrategy(boolean isUpStation) {
        if (isUpStation) {
            return new UpWardStrategy();
        }
        return new DownWardStrategy();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findSectionBylineId(lineId));
        sections.validDeletableSection();

        sectionDao.deleteByStationId(lineId, stationId);

        if (sections.notEndStation(stationId)) {
            Section sectionOfSameUpStation = findStrategy(true).selectedSection(sections.getSections(), stationId);
            Section sectionOfSameDownStation = findStrategy(false).selectedSection(sections.getSections(), stationId);

            sectionDao.save(lineId,
                    sectionOfSameDownStation.getUpStation().getId(),
                    sectionOfSameUpStation.getDownStation().getId(),
                    sectionOfSameUpStation.getDistance() + sectionOfSameDownStation.getDistance());
        }
    }

    @Transactional
    public void deleteSectionByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }
}
