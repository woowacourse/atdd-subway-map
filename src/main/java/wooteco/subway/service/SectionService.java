package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.ui.dto.SectionRequest;

@Service
@Transactional
public class SectionService {

    private static final String UP_SUBWAY = "상행역";
    private static final String DOWN_SUBWAY = "하행역";
    private static final String NO_EXISTS_SUBWAY_ERROR = "%s에 존재하지 않는 역을 등록할 수 없습니다. -> %d";
    private static final String NO_EXISTS_LINE_ERROR = "존재하지 않는 노선에 등록할 수 없습니다. -> %d";

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SectionService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public void create(Long lineId, SectionRequest sectionRequest) {
        validRequest(lineId, sectionRequest);
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section newSection = sectionRequest.toEntity(lineId);
        Optional<Section> updateSection = sections.findUpdateWhenAdd(newSection);

        sectionDao.save(newSection);
        updateSection.ifPresent(sectionDao::update);
    }

    private void validRequest(Long lineId, SectionRequest sectionRequest) {
        if (!stationDao.existsById(sectionRequest.getUpStationId())) {
            throw new IllegalArgumentException(
                    String.format(NO_EXISTS_SUBWAY_ERROR, UP_SUBWAY, sectionRequest.getUpStationId()));
        }
        if (!stationDao.existsById(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException(
                    String.format(NO_EXISTS_SUBWAY_ERROR, DOWN_SUBWAY, sectionRequest.getDownStationId()));
        }
        if (!lineDao.existsById(lineId)) {
            throw new IllegalArgumentException(
                    String.format(NO_EXISTS_LINE_ERROR, lineId));
        }
    }

    public void deleteById(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findByLineIdAndStationId(lineId, stationId);
        delete(new Sections(sections), stationId);
    }

    public void delete(Sections sections, Long stationId) {
        Optional<Section> updatedSection = sections.findUpdateWhenRemove(stationId);
        updatedSection.ifPresent(sectionDao::update);

        Section section = sections.findByDownStationId(stationId);
        sectionDao.deleteById(section.getId());
    }
}
