package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionRequest;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        validateSectionRequest(sectionRequest);

        Section section = Section.of(lineId, sectionRequest);
        sectionDao.save(section);
    }

    public List<Station> findStationsByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        List<Long> stationIds = sections.getSortedStationIds();

        List<Station> stations = new LinkedList<>();
        for (Long id : stationIds) {
            stations.add(stationDao.findById(id));
        }

        return stations;
    }

    public void add(Long lineId, SectionRequest sectionRequest) {
        validateSectionRequest(sectionRequest);

        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Section target = Section.of(lineId, sectionRequest);

        sections.validateTarget(target);

        if (sections.isTerminus(target)) {
            sectionDao.save(target);
            return;
        }

        processMiddle(sections, target);
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateDelete();

        List<Section> source = sections.findSectionByStationId(stationId);

        for (Section section : source) {
            sectionDao.delete(section.getId());
        }

        if (source.size() > 1) {
            Section combinedSection = source.get(0).combine(source.get(1));
            sectionDao.save(combinedSection);
        }
    }

    private void processMiddle(Sections sections, Section target) {
        Section source = sections.findSource(target);
        Section rest = source.makeRest(target);

        sectionDao.delete(source.getId());
        sectionDao.save(target);
        sectionDao.save(rest);
    }

    private void validateSectionRequest(SectionRequest request) {
        stationDao.findById(request.getUpStationId());
        stationDao.findById(request.getDownStationId());
    }
}
