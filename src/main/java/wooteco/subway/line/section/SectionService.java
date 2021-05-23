package wooteco.subway.line.section;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.section.dto.SectionRequest;
import wooteco.subway.station.StationDao;

@Service
@Transactional
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao,
        StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        Section section = getSection(sectionRequest);
        validateSave(lineId, section);

        List<Section> lineSections = findByLineId(lineId);
        updateSection(lineId, section, lineSections);

        sectionDao.save(lineId, section);
    }

    private void updateSection(Long lineId, Section section, List<Section> lineSections) {
        if (lineSections.size() == 0) {
            return;
        }
        Sections sections = getSortedSections(lineSections);
        Section resultSection = sections.findJoinResultSection(section);
        sectionDao.update(lineId, resultSection);
    }

    private Sections getSortedSections(List<Section> lineSections) {
        Sections sections = new Sections(lineSections);
        sections.sort();
        return sections;
    }

    private void validateSave(Long lindId, Section section) {
        if (!lineDao.isExistLine(lindId)) {
            throw new SubwayCustomException(SubwayException.NOT_EXIST_LINE_EXCEPTION);
        }
        if (!stationDao.isExistStations(section.getUpStationId(), section.getDownStationId())) {
            throw new SubwayCustomException(SubwayException.NOT_EXIST_STATION_EXCEPTION);
        }
    }

    private Section getSection(SectionRequest sectionRequest) {
        return new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
    }

    public void deleteByLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }

    public List<Section> findByLineId(Long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public void deleteByStationId(Long lineId, Long stationId) {
        List<Section> lineSections = findByLineId(lineId);
        Sections sections = getSortedSections(lineSections);

        Section deleteResultSection = sections.findDeleteResultSection(stationId);
        Section updateResultSection = sections
            .findDeleteUpdateResultSection(stationId, deleteResultSection);
        if (Objects.nonNull(updateResultSection)) {
            sectionDao.update(lineId, updateResultSection);
        }
        sectionDao.deleteById(lineId, deleteResultSection.getId());
    }
}
