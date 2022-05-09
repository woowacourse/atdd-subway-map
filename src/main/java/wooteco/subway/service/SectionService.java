package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.ui.dto.SectionRequest;

@Service
public class SectionService {

    public void create(Long lineId, SectionRequest sectionRequest) {
        // TODO 생성 전 검증 필요
    }

    public void deleteById(Long lineId, Long sectionId) {
        // TODO 삭제 후 재정렬 필요
    }
}
