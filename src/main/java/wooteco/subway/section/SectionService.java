package wooteco.subway.section;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.UniqueSectionDeleteException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;
import wooteco.subway.station.dto.StationResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao,
        StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    @Transactional
    public void createSectionOfNewLine(Long lineId, Long upStationId, Long downStationId,
        int distance) {
        Section section = makeSection(upStationId, downStationId, distance);
        sectionDao.save(lineId, section);
    }

    @Transactional
    public void create(Long lineId, Long upStationId, Long downStationId, int distance) {
        Section section = makeSection(upStationId, downStationId, distance);
        Sections sections = findSections(lineId);
        if (section.isEndPointOf(sections)) {
            sectionDao.save(lineId, section);
            return;
        }
        createSectionBetweenSections(lineId, section, sections);
    }

    public List<StationResponse> findAllByLineId(Long lineId) {
        Sections sections = findSections(lineId);
        List<Long> stationIds = convertToIds(sections.sortedStations());
        return stationService.findAllByIds(stationIds);
    }

    @Transactional
    public void deleteById(Long lineId, Long stationId) {
        if (isUniqueSectionOfLine(lineId)) {
            throw new UniqueSectionDeleteException();
        }
        Sections sections = convertToSection(sectionDao.findById(lineId, stationId));
        sectionDao.deleteByStationId(lineId, stationId);
        if (sections.isNotEndPoint()) {
            Station upStation = sections.findUpStation(stationId);
            Station downStation = sections.findDownStation(stationId);
            sectionDao
                .save(lineId, new Section(upStation, downStation, sections.sumDistance()));
        }
    }

    @Transactional
    public void deleteAllByLineId(Long lineId) {
        sectionDao.deleteAllById(lineId);
    }

    private Section makeSection(Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        return new Section(upStation, downStation, distance);
    }

    private void createSectionBetweenSections(Long lineId, Section section, Sections sections) {
        Section dividedSection = sections.divideSection(section);
        sectionDao.deleteById(dividedSection.getId());
        sectionDao.save(lineId, dividedSection);
        sectionDao.save(lineId, section);
    }

    private Sections findSections(Long lineId) {
        return convertToSection(sectionDao.findSectionsByLineId(lineId));
    }

    private Sections convertToSection(List<SectionResponse> responses) {
        return responses.stream()
            .map(response -> new Section(response.getId()
                , stationService.findById(response.getUpStationId())
                , stationService.findById(response.getDownStationId())
                , response.getDistance()))
            .collect(collectingAndThen(toList(), Sections::new))
            ;
    }

    private List<Long> convertToIds(List<Station> sortedStations) {
        return sortedStations.stream()
            .map(Station::getId)
            .collect(toList());
    }

    private boolean isUniqueSectionOfLine(Long lineId) {
        return sectionDao.findSectionsByLineId(lineId).size() == 1;
    }
}
