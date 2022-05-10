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

        Optional<Section> sectionByUpStation = sectionDao.findByLineIdAndUpStationId(id, request.getUpStationId());
        Optional<Section> sectionByDownStation =
                sectionDao.findByLineIdAndDownStationId(id, request.getDownStationId());

        checkCanAddSection(sectionByUpStation, sectionByDownStation);
        updateIfForkLine(id, request, sectionByUpStation, sectionByDownStation);

        sectionDao.save(new Section(id, upStationId, downStationId, distance));
    }

    private void checkCanAddSection(Optional<Section> sectionByUpStation, Optional<Section> sectionByDownStation) {
        if (sectionByUpStation.isPresent() && sectionByDownStation.isPresent()) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
    }

    private void updateIfForkLine(Long id,
                                  SectionRequest request,
                                  Optional<Section> sectionByUpStation,
                                  Optional<Section> sectionByDownStation) {
        if (sectionByUpStation.isPresent()) {
            Section section = sectionByUpStation.get();
            int distance = section.getDistance() - request.getDistance();
            validateDistance(distance);

            sectionDao.update(section.getId(), new Section(
                    id,
                    request.getDownStationId(),
                    section.getDownStationId(),
                    distance));
            return;
        }
        if (sectionByDownStation.isPresent()) {
            Section section = sectionByDownStation.get();
            int distance = request.getDistance() - section.getDistance();
            validateDistance(distance);

            sectionDao.update(section.getId(), new Section(
                    id,
                    section.getUpStationId(),
                    request.getUpStationId(),
                    distance));
        }
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("역 사이 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }
}
