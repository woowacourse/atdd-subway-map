import { EVENT_TYPE } from "../../utils/constants.js";
import { colorSelectOptionTemplate, subwayLinesTemplate } from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  const $subwayLineInfoContainer = document.querySelector("#line-info-container");
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  const onCreateSubwayLine = async (event) => {
    event.preventDefault();

    const lineRequest = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      bgColor: $subwayLineColorInput.value
    };

    const lineResponse = await api.line.create(lineRequest);

    const newSubwayLine = {
      id: lineResponse.id,
      name: lineResponse.name,
      bgColor: lineResponse.bgColor
    };

    $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate(newSubwayLine)
    );
    subwayLineModal.toggle();
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    event.preventDefault();
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $lineItem = $target.closest(".subway-line-item");
      const lineId = $lineItem.querySelector(".line-id").value;

      api.line.delete(lineId).then(
        () => $lineItem.remove(),
        reason => alert(reason)
      );
    }
  };

  const onUpdateSubwayLine = event => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      subwayLineModal.toggle();
    }
  };

  const onReadSubwayInfo = async (event) => {
    const $target = event.target;
    const isSubwayLineItem = $target.classList.contains("subway-line-item");
    const lineInfo = $subwayLineInfoContainer.querySelector(".lines-info");

    if (lineInfo) {
      lineInfo.remove();
    }

    if (isSubwayLineItem) {
      const $lineItem = $target.closest(".subway-line-item");
      const lineId = $lineItem.querySelector(".line-id").value;
      const readResponse = await api.line.getById(lineId);

      const lineInfoTemplate = (first, last, interval) => `<div class="lines-info flex flex-wrap mb-3 w-full">
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">첫차시간</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${first}</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">막차시간</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${last}</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">간격</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${interval}</div>
        </div>`;

      $subwayLineInfoContainer.insertAdjacentHTML("afterbegin",
        lineInfoTemplate(readResponse.startTime, readResponse.endTime, readResponse.intervalTime));
    }
  }

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-pencil");
  };

  const initDefaultSubwayLines = () => {
    defaultSubwayLines.map(line => {
      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(line)
      );
    });
  };

  const initSubwayLines = async () => {
    const initLines = await api.line.get();

    initLines.map(line => {
      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(line)
      );
    });
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onReadSubwayInfo)
    $createSubwayLineButton.addEventListener(
      EVENT_TYPE.CLICK,
      onCreateSubwayLine
    );
  };

  const onSelectColorHandler = event => {
    event.preventDefault();
    const $target = event.target;
    if ($target.classList.contains("color-select-option")) {
      document.querySelector("#subway-line-color").value =
        $target.dataset.color;
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
    $colorSelectContainer.addEventListener(
      EVENT_TYPE.CLICK,
      onSelectColorHandler
    );
  };

  this.init = () => {
    initSubwayLines();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
