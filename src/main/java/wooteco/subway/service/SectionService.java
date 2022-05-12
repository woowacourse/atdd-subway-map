package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private static final int END_SECTION = 1;

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public void add(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationDao.findById(sectionRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionRequest.getDownStationId());
        Line line = lineDao.findById(lineId);
        Section newSection = new Section(line.getId(), upStation.getId(), downStation.getId(),
                sectionRequest.getDistance());
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section section = sectionDao.save(newSection);
        sections.add(section);
        sectionDao.update(sections.getValue());
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Section> deleteSections = sections.delete(stationId);

        if (isEndSection(deleteSections)) {
            Section section = deleteSections.get(0);
            sectionDao.deleteById(section.getId());
            return;
        }
        deleteBetweenStation(deleteSections);
    }

    private void deleteBetweenStation(List<Section> deleteSections) {
        Section section = deleteSections.get(0);
        Section nextSection = deleteSections.get(1);

        sectionDao.deleteById(section.getId());
        sectionDao.deleteById(nextSection.getId());
        Section mergeSection = section.merge(nextSection);
        sectionDao.save(mergeSection);
    }

    private boolean isEndSection(List<Section> deleteSections) {
        return deleteSections.size() == END_SECTION;
    }
}
