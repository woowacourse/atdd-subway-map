package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public List<Station> findUniqueSectionStationsByLineId(final Long lineId) {
        return sectionDao.findSectionStationsByLineId(lineId)
            .stream()
            .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
            .distinct()
            .map(id -> stationDao.findById(id).get())
            .sorted()
            .collect(Collectors.toList());
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Section targetSection = sectionRequest.toEntity(lineId);
        //트랜잭셔널을 달았으므로, 일단 section Id부여를 위해 (Id 부여되있어야 일괄 업데이트 됨)
        targetSection = sectionDao.save(targetSection);
        final Sections sections = new Sections(sectionDao.findSectionStationsByLineId(lineId));

        //1) sections - station id별로 정렬해서 나열한다
        final List<Section> updatedSections = sections.addSection(targetSection);
    }
}
