package wooteco.subway.section.sevice;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.section.model.Sections;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.dao.StationDao;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionRepository sectionRepository;

    public SectionService(LineDao lineDao, StationDao stationDao,
        SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void save(Long lineId, SectionRequest sectionRequest) {
        List<Section> findSections = sectionRepository.findSectionsByLineId(lineId);
        Sections sections = new Sections(findSections);
        sections.add(convertToSection(lineId, sectionRequest));
        updateSections(lineId, sections);
    }

    private Section convertToSection(Long lineId, SectionRequest sectionRequest) {
        return Section.builder()
            .line(lineDao.findLineById(lineId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 노선 ID 입니다.")))
            .upStation(stationDao.findStationById(sectionRequest.getUpStationId()))
            .downStation(stationDao.findStationById(sectionRequest.getDownStationId()))
            .distance(sectionRequest.getDistance())
            .build();
    }

    @Transactional
    public void deleteById(Long lineId, Long stationId) {
        List<Section> findSections = sectionRepository.findSectionsByLineId(lineId);
        Sections sections = new Sections(findSections);
        sections.delete(stationId);
        updateSections(lineId, sections);
    }

    private void updateSections(Long lineId, Sections sections) {
        sectionRepository.deleteAllByLineId(lineId);
        sectionRepository.saveAll(sections.sections());
    }
}
