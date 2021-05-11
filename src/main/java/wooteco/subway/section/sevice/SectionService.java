package wooteco.subway.section.sevice;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.api.dto.SectionDto;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.section.model.Sections;
import wooteco.subway.station.dao.StationDao;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void save(Long lineId, SectionRequest sectionRequest) {
        List<SectionDto> sectionDtos = sectionDao.findSectionsByLineId(lineId);
        Sections sections = new Sections(mapToSections(sectionDtos));
        sections.add(convertToSection(lineId, sectionRequest));
        updateSections(lineId, sections);
    }

    private List<Section> mapToSections(List<SectionDto> sectionDtos) {
        return sectionDtos.stream()
            .map(sectionDto -> new Section(lineDao.findLineById(sectionDto.getLineId()),
                stationDao.findStationById(sectionDto.getUpStationId()),
                stationDao.findStationById(sectionDto.getDownStationId()),
                sectionDto.getDistance()))
            .collect(Collectors.toList());
    }

    private Section convertToSection(Long lineId, SectionRequest sectionRequest) {
        return new Section(lineDao.findLineById(lineId),
            stationDao.findStationById(sectionRequest.getUpStationId()),
            stationDao.findStationById(sectionRequest.getDownStationId()),
            sectionRequest.getDistance());
    }

    @Transactional
    public void deleteById(Long lineId, Long stationId) {
        List<SectionDto> sectionDtos = sectionDao.findSectionsByLineId(lineId);
        Sections sections = new Sections(mapToSections(sectionDtos));
        sections.delete(stationId);
        updateSections(lineId, sections);
    }

    private void updateSections(Long lineId, Sections sections) {
        sectionDao.deleteAllByLineId(lineId);
        sectionDao.saveAll(sections.sections());
    }
}
