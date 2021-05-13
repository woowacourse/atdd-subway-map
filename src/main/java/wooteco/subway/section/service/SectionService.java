package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dao.SectionDto;
import wooteco.subway.section.domain.OrderedSections;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Service
public class SectionService {
    private final StationService stationService;

    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void add(Long lineId, Long upStationId, Long downStationId, int distance) {
        Section section = save(lineId, upStationId, downStationId, distance);
        OrderedSections lineSections = findSections(lineId);
        sectionDao.saveAll(lineId, lineSections.addSection(section));
    }

    private Section save(Long lineId, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);

        return sectionDao.save(lineId, new Section(upStation, downStation, distance));
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        Station byId = stationService.findById(stationId);
        OrderedSections sections = findSections(lineId);

        sectionDao.saveAll(lineId, sections.removeSection(byId));
    }

    public OrderedSections findSections(Long lineId) {
        return convert(sectionDao.findByLineId(lineId));
    }

    private OrderedSections convert(List<SectionDto> sectionDtos) {
        return sectionDtos.stream()
                .map(sectionDto -> new Section(sectionDto.getId()
                        , stationService.findById(sectionDto.getUpStationId())
                        , stationService.findById(sectionDto.getDownStationId())
                        , sectionDto.getDistance()))
                .collect(collectingAndThen(toList(), OrderedSections::new));
    }

    @Transactional
    public void removeLine(Long lineId) {
        sectionDao.deleteLine(lineId);
    }
}
