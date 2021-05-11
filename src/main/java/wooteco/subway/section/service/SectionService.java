package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dao.SectionDto;
import wooteco.subway.section.domain.OrderedSections;
import wooteco.subway.section.domain.Section;
import wooteco.subway.station.service.StationService;
import wooteco.subway.station.domain.Station;

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
    public void save(Long lineId, Long upStationId, Long downStationId, int distance) {
        OrderedSections lineSections = convert(sectionDao.findByLineId(lineId));
        Section section = create(lineId, upStationId, downStationId, distance);
        sectionDao.save(lineId, lineSections.addSection(section));
    }

    public Section create(Long lineId, Long upStationId, Long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);

        return sectionDao.save(lineId, new Section(upStation, downStation, distance));
    }

    public OrderedSections findSections(long lineId) {
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

    public void delete(long stationId) {
        Station byId = stationService.findById(stationId);
        stationService.delete(byId);
    }
}
