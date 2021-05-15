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
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@Service
@Transactional
public class SectionService {
    private final StationService stationService;

    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public OrderedSections add(Long lineId, Long upStationId, Long downStationId, Long distance) {
        save(lineId, upStationId, downStationId, distance);
        OrderedSections lineSections = findSections(lineId);
        sectionDao.saveAll(lineId, lineSections);
        return lineSections;
    }

    private Section save(Long lineId, Long upStationId, Long downStationId, Long distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);

        return sectionDao.save(lineId, new Section(upStation, downStation, distance));
    }

    public void delete(Long lineId, Long stationId) {
        Station byId = stationService.findById(stationId);
        OrderedSections sections = findSections(lineId);

        sectionDao.saveAll(lineId, sections.removeSection(byId));
    }

    public OrderedSections findSections(Long lineId) {
        return convert(sectionDao.findByLineId(lineId));
    }

    public Map<Long, OrderedSections> findSectionsWithLineId() {
        Map<Long, List<SectionDto>> all = sectionDao.findAll();
        Map<Long, OrderedSections> lineIdAndSections = all.keySet().stream()
                .collect(toMap(Function.identity(), lineId -> convert(all.get(lineId))));

        return lineIdAndSections;
    }

    private OrderedSections convert(List<SectionDto> sectionDtos) {
        return sectionDtos.stream()
                .map(sectionDto -> new Section(sectionDto.getId()
                        , stationService.findById(sectionDto.getUpStationId())
                        , stationService.findById(sectionDto.getDownStationId())
                        , sectionDto.getDistance()))
                .collect(collectingAndThen(toList(), OrderedSections::new));
    }

    public void deleteSections(Long lineId) {
        sectionDao.deleteSections(lineId);
    }
}
