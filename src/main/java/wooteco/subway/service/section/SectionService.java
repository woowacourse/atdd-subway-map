package wooteco.subway.service.section;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.request.SectionRequest;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao,
        StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void createSection(LineRequest lineRequest, Long lineId) {
        Station upStation = stationDao.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역입니다."));
        Station downStation = stationDao.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역입니다."));
        Section section = new Section(lineId, upStation, downStation, lineRequest.getDistance());
        sectionDao.save(section);
    }

    @Transactional
    public void deleteSectionsByLineId(Long id) {
        List<Section> sections = sectionDao.findByLineId(id);
        sections.forEach(section -> sectionDao.deleteById(section.getId()));
    }

    @Transactional(readOnly = true)
    public List<Section> findByLineId(Long id) {
        return sectionDao.findByLineId(id);
    }

    @Transactional
    public void addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationDao.findById(sectionRequest.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역입니다."));
        Station downStation = stationDao.findById(sectionRequest.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역입니다."));
        Section newSection = new Section(id, upStation, downStation, sectionRequest.getDistance());

        Sections sections = new Sections(sectionDao.findByLineId(id));
        if (sections.canAddToEndSection(newSection)) {
            sectionDao.save(newSection);
            return;
        }
        sectionDao.save(newSection);
        Section updateSection = sections.addToBetweenExistedSection(newSection);
        sectionDao.update(updateSection);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Station requestStation = stationDao.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역입니다."));
        if (sections.canRemoveEndSection(requestStation)) {
            Section removeSection = sections.findUpdateAndRemoveSections(requestStation).get(0);
            sectionDao.deleteById(removeSection.getId());
            return;
        }
        List<Section> updateAndRemoveSections = sections.findUpdateAndRemoveSections(requestStation);
        Section updateSection = updateAndRemoveSections.get(0);
        Section removeSection = updateAndRemoveSections.get(1);
        sectionDao.update(updateSection.mergeAndUpdate(removeSection));
        sectionDao.deleteById(removeSection.getId());
    }
}
