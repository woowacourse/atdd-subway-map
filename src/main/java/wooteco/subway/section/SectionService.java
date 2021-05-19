package wooteco.subway.section;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.UniqueSectionDeleteException;
import wooteco.subway.line.Line;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.SectionDto;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;
import wooteco.subway.station.dto.StationResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao,
        LineDao lineDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationService = stationService;
    }

    @Transactional
    public void create(Long lineId, Long upStationId, Long downStationId, int distance) {
        Section section = makeSection(lineId, upStationId, downStationId, distance);
        Sections sections = findSections(lineId);
        if (isNewLine(lineId) || section.isEndPointOf(sections)) {
            sectionDao.save(lineId, section);
            return;
        }
        createSectionBetweenSections(lineId, section, sections);
    }

    private boolean isNewLine(Long lineId) {
        return sectionDao.countById(lineId) == 0;
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
            Line line = lineDao.findById(lineId);
            Station upStation = sections.findUpStation(stationId);
            Station downStation = sections.findDownStation(stationId);
            sectionDao
                .save(lineId, new Section(line, upStation, downStation, sections.sumDistance()));
        }
    }

    @Transactional
    public void deleteAllByLineId(Long lineId) {
        sectionDao.deleteAllById(lineId);
    }

    private Section makeSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Line line = lineDao.findById(lineId);
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        return new Section(line, upStation, downStation, distance);
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

    private Sections convertToSection(List<SectionDto> responses) {
        return responses.stream()
            .map(response -> new Section(response.getId()
                , lineDao.findById(response.getLineId())
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
