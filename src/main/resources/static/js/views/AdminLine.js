import { colorSelectOptionTemplate, subwayLinesTemplate } from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from '../../api/index.js';

function AdminLine() {
  // list
  const $subwayLineList = document.querySelector("#subway-line-list");

  // detail
  const $subwayLineFirstTimeInfo = document.querySelector("#first-time-info");
  const $subwayLineLastTimeInfo = document.querySelector("#last-time-info");
  const $subwayLineIntervalTimeInfo = document.querySelector("#interval-time-info");

  // create & update modal
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $subwayLineUpdateId = document.querySelector("#line-update-id");
  const $submitSubwayLineButton = document.querySelector("#submit-button");
  const $cancelSubwayLineButton = document.querySelector("#cancel-button");

  let $currentSubwayLineItem;
  let isEdit = false;

  const subwayLineModal = new Modal();

  const onSubmitSubwayLine = event => {
    event.preventDefault();
    let inputSubwayLine = {
      name: $subwayLineNameInput.value,
      color: $subwayLineColorInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value
    };
    if (isEdit) {
      inputSubwayLine.id = $subwayLineUpdateId.value;
    }

    sendNewLine(inputSubwayLine).then(() => location.reload());

    const newLineTemplate = subwayLinesTemplate({
      title: inputSubwayLine.name,
      bgColor: inputSubwayLine.color
    });

    const $subwayLineItem = document.createElement('div');
    $subwayLineItem.innerHTML = newLineTemplate;

    if (isEdit) {
      $subwayLineList.replaceChild($subwayLineItem.firstChild, $currentSubwayLineItem);
    } else {
      $subwayLineList.insertAdjacentHTML("beforeend", newLineTemplate);
    }
    subwayLineModal.toggle();
    clearForm();
  };

  const sendNewLine = data => {
    if (isEdit) {
      return api.line.update(data.id, data);
    }
    return api.line.create(data);
  };

  const clearForm = () => {
    $subwayLineFirstTimeInput.value = "";
    $subwayLineLastTimeInput.value = "";
    $subwayLineIntervalTimeInput.value = "";
    $subwayLineUpdateId.value = "";
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
  }

  const onDeleteSubwayLine = event => {
    if (event.target && event.target.classList.contains("mdi-delete")) {
      const $subwayLineItem = event.target.closest(".subway-line-item");
      const lineId = $subwayLineItem.dataset.lineId;

      $subwayLineItem.remove();
      return api.line.delete(lineId);
    }
  };

  const onEditSubwayLine = async event => {
    event.preventDefault();
    if (event.target && event.target.classList.contains("mdi-pencil")) {
      isEdit = true;
      $currentSubwayLineItem = event.target.closest("div");
      const lineId = $currentSubwayLineItem.dataset.lineId;
      const line = await api.line.getLine(lineId);

      $subwayLineUpdateId.value = line.id;
      $subwayLineNameInput.value = line.name;
      $subwayLineColorInput.value = line.color;
      $subwayLineFirstTimeInput.value = line.startTime;
      $subwayLineLastTimeInput.value = line.endTime;
      $subwayLineIntervalTimeInput.value = line.intervalTime;
      subwayLineModal.toggle();
    }
  };

  const onDetailSubwayLine = async event => {
    event.preventDefault();
    if (event.target && event.target.classList.contains("subway-line-item")) {
      const lineId = event.target.dataset.lineId;
      const line = await api.line.getLine(lineId);

      $subwayLineFirstTimeInfo.innerText = line.startTime;
      $subwayLineLastTimeInfo.innerText = line.endTime;
      $subwayLineIntervalTimeInfo.innerText = line.intervalTime;
    }
  }

  const onCancelSubwayLine = () => {
    clearForm();
  }

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDetailSubwayLine);
    $submitSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onSubmitSubwayLine);
    $cancelSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onCancelSubwayLine);
  };

  const onSelectColorHandler = event => {
    event.preventDefault();
    const $target = event.target;

    if ($target.classList.contains("color-select-option")) {
      document.querySelector("#subway-line-color").value = $target.dataset.color;
    }
  };

  const initCreateSubwayLineForm = () => {
    const $colorSelectContainer = document.querySelector("#subway-line-color-select-container");
    const colorSelectTemplate = subwayLineColorOptions
    .map((option, index) => colorSelectOptionTemplate(option, index))
    .join("");

    $colorSelectContainer.insertAdjacentHTML("beforeend", colorSelectTemplate);
    $colorSelectContainer.addEventListener(EVENT_TYPE.CLICK, onSelectColorHandler);
  };

  this.init = () => {
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
