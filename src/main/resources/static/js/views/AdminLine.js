import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  const $subwayLinesInfo = document.querySelector(".lines-info");
  const $subwayLineFirstTimeView = $subwayLinesInfo.children[1];
  const $subwayLineLastTimeView = $subwayLinesInfo.children[3];
  const $subwayLineIntervalTimeView = $subwayLinesInfo.children[5];

  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $subwayLineAddButton = document.querySelector("#subway-line-add-btn")

  const $saveSubwayLineButton = document.querySelector(
      "#subway-line-create-form #submit-button"
  );

  const subwayLineModal = new Modal();

  let $activeSubwayLineItem = null;

  const onCreateSubwayLine = () => {
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      color: $subwayLineColorInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value
    };
    api.line.create(newSubwayLine).then(line => {
      if (!line.name) {
        return;
      }
      $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(line)
      );
    });
    subwayLineModal.toggle();
  };

  const viewTime = time => {
    const colon = ":";
    const timeArray = time.split(colon);
    return timeArray[0] + colon + timeArray[1];
  }

  const onSelectSubwayLine = event => {
    const $target = event.target;
    const isSubwayLineItem = $target.classList.contains("subway-line-item");
    if (isSubwayLineItem) {
      api.line.get($target.dataset.id).then(line => {
        $subwayLineFirstTimeView.innerText = viewTime(line.startTime);
        $subwayLineLastTimeView.innerText = viewTime(line.endTime);
        $subwayLineIntervalTimeView.innerText = `${line.intervalTime}분`;
      });
    }
  };

  const onUpdateSubwayLine = () => {
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      color: $subwayLineColorInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value
    };
    api.line.update(newSubwayLine, $activeSubwayLineItem.dataset.id).then(line => {
      if (!line.name) {
        return;
      }
      const $subwayLineItemTemplate = document.createElement('div');
      $subwayLineItemTemplate.innerHTML = subwayLinesTemplate(line);
      $subwayLineList.insertBefore($subwayLineItemTemplate.firstChild, $activeSubwayLineItem);
      $activeSubwayLineItem.remove();
      $activeSubwayLineItem = null;
    });
    subwayLineModal.toggle();
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $subwayLineItem = $target.closest(".subway-line-item");
      api.line.delete($subwayLineItem.dataset.id).then(() => {
        $target.closest(".subway-line-item").remove();
      });
    }
  };

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      $activeSubwayLineItem = $target.closest(".subway-line-item");
      api.line.get($activeSubwayLineItem.dataset.id).then(line => {
        subwayLineModal.toggle();
        $subwayLineNameInput.value = line.name;
        $subwayLineColorInput.value = line.color;
        $subwayLineFirstTimeInput.value = line.startTime;
        $subwayLineLastTimeInput.value = line.endTime;
        $subwayLineIntervalTimeInput.value = line.intervalTime;
      });
    }
  };

  const onAddSubwayLine = event => {
    $activeSubwayLineItem = null;
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
    $subwayLineFirstTimeInput.value = "";
    $subwayLineLastTimeInput.value = "";
    $subwayLineIntervalTimeInput.value = "";
  };

  const onSaveSubwayLine = event => {
    event.preventDefault();
    $activeSubwayLineItem ? onUpdateSubwayLine() : onCreateSubwayLine();
  };

  const initDefaultSubwayLines = () => {
    api.line.get().then(lines => {
      lines.map(line => {
        $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(line)
        );
      });
    });
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $saveSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onSaveSubwayLine);
    $subwayLineAddButton.addEventListener(EVENT_TYPE.CLICK, onAddSubwayLine);
  };

  const onSelectColorHandler = event => {
    event.preventDefault();
    const $target = event.target;
    if ($target.classList.contains("color-select-option")) {
      document.querySelector("#subway-line-color").value = $target.dataset.color;
    }
  };

  const initCreateSubwayLineForm = () => {
    const $colorSelectContainer = document.querySelector(
        "#subway-line-color-select-container"
    );
    const colorSelectTemplate = subwayLineColorOptions
        .map((option, index) => colorSelectOptionTemplate(option, index))
        .join("");
    $colorSelectContainer.insertAdjacentHTML("beforeend", colorSelectTemplate);
    $colorSelectContainer.addEventListener(EVENT_TYPE.CLICK, onSelectColorHandler);
  };

  const init = () => {
    initDefaultSubwayLines();
    initEventListeners();
    initCreateSubwayLineForm();
  };

  return {
    init
  };
}

const adminLine = new AdminLine();
adminLine.init();
