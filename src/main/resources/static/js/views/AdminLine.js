import { EVENT_TYPE, ERROR_MESSAGE } from "../../utils/constants.js";
import { subwayLinesTemplate, detailSubwayLineTemplate } from "../../utils/templates.js";
import CreateSubWayLineModal from "../../ui/CreateSubwayLineModal.js"
import api from "../../api/index.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $linesInfo = document.querySelector(".lines-info");
  const $subwayLineAddBtn = document.querySelector("#subway-line-add-btn");
  
  const subwayLineModal = new CreateSubWayLineModal();

  let $activeSubwayLineItem = null;

  const onSaveSubwayLine = data => {
    $activeSubwayLineItem ? updateSubwayLine(data) : createSubwayLine(data);
  };

  const createSubwayLine = async data => {
  try{
    const { id } = await api.line.create(data);
    $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate({
        id,
        ...data
      })
    );
    subwayLineModal.toggle();
    } catch (e) {
      alert(e.message);
    }
  };

  const updateSubwayLine = async data => {
    const id = $activeSubwayLineItem.dataset.lineId;
    try {
      await api.line.update(data, id);
      const $newSubwayLineParent = document.createElement("div");
      $newSubwayLineParent.innerHTML = subwayLinesTemplate({
        id,
        ...data
      });
      $subwayLineList.insertBefore($newSubwayLineParent.firstElementChild, $activeSubwayLineItem);
      $activeSubwayLineItem.remove();
      subwayLineModal.toggle();
    } catch (e) {
      alert(e.message);
    }
  };

  const onDeleteSubwayLine = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $subwayLineItem = $target.closest(".subway-line-item");
      try {
        await api.line.delete($subwayLineItem.dataset.lineId);
        $subwayLineItem.remove();
      } catch (e) {
        alert(e.message);
      }
    }
  };

  const onSelectSubwayLine = async event => {
    const $target = event.target;
    const isSubwayLineItem = $target.classList.contains("subway-line-item");
    if (isSubwayLineItem) {
      try {
        const line = await api.line.get($target.dataset.lineId);
        $linesInfo.innerHTML = detailSubwayLineTemplate(line);
      } catch (e) {
        alert(e.message);
      }
    }
  }

  const onEditSubwayLine = async event => {
    const $target = event.target;
    const isEditButton = $target.classList.contains("mdi-pencil");
    if (isEditButton) {
      $activeSubwayLineItem = $target.closest(".subway-line-item");
      try {
        const line = await api.line.get($activeSubwayLineItem.dataset.lineId);
        subwayLineModal.toggle();
        subwayLineModal.setData(line);
      } catch (e) {
        alert(e.message);
      }
    }
  };

  const initDefaultSubwayLines = async () => {
    try {
      const lines = await api.line.get();
      lines.map(line => {
        $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(line)
        );
      });
    } catch (e) {
      alert(e.message);
    }
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
    $subwayLineAddBtn.addEventListener(EVENT_TYPE.CLICK, () => {
      subwayLineModal.clear();
      $activeSubwayLineItem = null;
    });
    subwayLineModal.on('submit', onSaveSubwayLine);
  };

  this.init = () => {
    initDefaultSubwayLines();
    initEventListeners();
    subwayLineModal.init();
  };
}

const adminLine = new AdminLine();
adminLine.init();
