package wooteco.subway.domain.exception;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;

public class UnsplittableException extends IllegalArgumentException {

    public UnsplittableException(Section section1, Section section2) {
        super(String.format("구간 %s와 구간 %s를 분리할 수 없습니다.", section1, section2));
    }

    public UnsplittableException(SectionEdge edge1, SectionEdge edge2) {
        super(String.format("간선 %s와 간선 %s를 분리할 수 없습니다.", edge1, edge2));
    }
}
