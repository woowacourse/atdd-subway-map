package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class SectionBuffer {

    private List<Section> addBuffer;
    private List<Section> deleteBuffer;

    public SectionBuffer() {
        addBuffer = new ArrayList<>();
        deleteBuffer = new ArrayList<>();
    }

    public void addToAddBuffer(Section section) {
        addBuffer.add(section);
    }

    public void addToDeleteBuffer(Section section) {
        deleteBuffer.add(section);
    }

    public List<Section> getAddBuffer() {
        return addBuffer;
    }

    public List<Section> getDeleteBuffer() {
        return deleteBuffer;
    }
}
