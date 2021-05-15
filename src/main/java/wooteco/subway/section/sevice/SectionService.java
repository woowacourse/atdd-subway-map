package wooteco.subway.section.sevice;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.section.model.SectionRepository;
import wooteco.subway.section.model.Sections;
import wooteco.subway.station.dao.StationDao;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final SectionRepository sectionRepository;

    public SectionService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao,
        SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void save(Long lineId, SectionRequest sectionRequest) {
        List<Section> findSections = sectionRepository.findSectionsByLineId(lineId);
        Sections sections = new Sections(findSections);
        sections.add(convertToSection(lineId, sectionRequest));
        updateSections(lineId, sections);
    }

    @Transactional
    public void deleteById(Long lineId, Long stationId) {
        List<Section> findSections = sectionRepository.findSectionsByLineId(lineId);
        Sections sections = new Sections(mapToSections(findSections));
        sections.delete(stationId);
        updateSections(lineId, sections);
    }

    private List<Section> mapToSections(List<Section> sectionDtos) {
        return sectionDtos.stream()
            .map(sectionDto -> new Section(lineDao.findLineById(sectionDto.getLineId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 노선 ID 입니다.")),
                stationDao.findStationById(sectionDto.getUpStationId()),
                stationDao.findStationById(sectionDto.getDownStationId()),
                sectionDto.getDistance()))
            .collect(Collectors.toList());
    }

    private Section convertToSection(Long lineId, SectionRequest sectionRequest) {
        return new Section(lineDao.findLineById(lineId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 노선 ID 입니다.")),
            stationDao.findStationById(sectionRequest.getUpStationId()),
            stationDao.findStationById(sectionRequest.getDownStationId()),
            sectionRequest.getDistance());
    }

    private void updateSections(Long lineId, Sections sections) {
        sectionDao.deleteAllByLineId(lineId);
        sectionDao.saveAll(sections.sections());
    }
}
