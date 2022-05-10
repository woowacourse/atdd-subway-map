package wooteco.subway.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void addSection(Long id, SectionRequest request) {
        Long upStationId = request.getUpStationId();
        Long downStationId = request.getDownStationId();
        int distance = request.getDistance();

        updateIfForkLine(id, request);

        sectionDao.save(new Section(id, upStationId, downStationId, distance));
    }

    private void updateIfForkLine(Long id, SectionRequest request) {
        Optional<Section> sectionByUpStation = sectionDao.findByLineIdAndUpStationId(id, request.getUpStationId());
        Optional<Section> sectionByDownStation =
                sectionDao.findByLineIdAndDownStationId(id, request.getDownStationId());

        if (sectionByUpStation.isPresent()) {
            Section section = sectionByUpStation.get();
            sectionDao.update(section.getId(), new Section(
                    id,
                    request.getDownStationId(),
                    section.getDownStationId(),
                    section.getDistance() - request.getDistance()));
            return;
        }
        if (sectionByDownStation.isPresent()) {
            Section section = sectionByDownStation.get();
            sectionDao.update(section.getId(), new Section(
                    id,
                    section.getUpStationId(),
                    request.getUpStationId(),
                    request.getDistance() - section.getDistance()));
        }
    }
}
