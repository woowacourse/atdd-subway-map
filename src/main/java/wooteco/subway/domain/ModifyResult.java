package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class ModifyResult {

    private final List<Section> saveResult;
    private final List<Section> deleteResult;

    public ModifyResult(List<Section> saveResult, List<Section> deleteResult) {
        this.saveResult = saveResult;
        this.deleteResult = deleteResult;
    }

    public static ModifyResult addResult(ModifyResult addedOne, ModifyResult addedOther) {
        List<Section> newSaved = new ArrayList<>();
        List<Section> newDelete = new ArrayList<>();
        newSaved.addAll(addedOne.getSaveResult());
        newSaved.addAll(addedOther.getSaveResult());
        newDelete.addAll(addedOne.getDeleteResult());
        newDelete.addAll(addedOther.getDeleteResult());
        return new ModifyResult(newSaved, newDelete);
    }

    public List<Section> getSaveResult() {
        return saveResult;
    }

    public List<Section> getDeleteResult() {
        return deleteResult;
    }
}
