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
        final Section targetSection = sectionRequest.toEntity(lineId);
        // 기존에 존재하는 sections을 가져와서 시작/끝을 일일히 확인한다. 가장 시작과 가장끝을 확인할 수 있나?
        //객체 vs List객체 비교를 위해, 일급컬렉션 생성
        //final List<Section> sectionList = sectionDao.findUniqueSectionStationsByLineId(lineId);
        final Sections sections = new Sections(sectionDao.findSectionStationsByLineId(lineId));

        //1) sections - station id별로 정렬해서 나열한다
        sections.canAddSection(targetSection);
    }
}
