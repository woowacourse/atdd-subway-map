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
public class SectionService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SectionService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void create(Long lineId, SectionRequest sectionRequest) {
        validRequest(lineId, sectionRequest);
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section newSection = sectionRequest.toEntity(lineId);
        Optional<Section> updateSection = sections.add(newSection);

        sectionDao.save(newSection);
        updateSection.ifPresent(sectionDao::update);
    }

    private void validRequest(Long lineId, SectionRequest sectionRequest) {
        if (!stationDao.existsById(sectionRequest.getUpStationId())) {
            throw new IllegalArgumentException(
                    String.format("상행역에 존재하지 않는 역을 등록할 수 없습니다. -> %d", sectionRequest.getUpStationId()));
        }
        if (!stationDao.existsById(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException(
                    String.format("하행역에 존재하지 않는 역을 등록할 수 없습니다. -> %d", sectionRequest.getDownStationId()));
        }
        if (!lineDao.existsById(lineId)) {
            throw new IllegalArgumentException(
                    String.format("존재하지 않는 노선에 등록할 수 없습니다. -> %d", lineId));
        }
    }

    @Transactional
    public void deleteById(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findByStationId(lineId, stationId);
        Long updatedId = sections.get(0).getDownStationId();

        for (Section section : sections) {
            if (section.isSameDownStationId(stationId)) {
                updatedId = section.getUpStationId();
                sectionDao.deleteById(section.getId());
                continue;
            }
            if (section.isSameUpStationId(stationId)) {
                section.updateUpStationId(updatedId);
                sectionDao.update(section);
            }
        }
    }
}
