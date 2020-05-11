import { EVENT_TYPE } from "../../utils/constants.js";
import { colorSelectOptionTemplate, subwayLinesTemplate } from "../../utils/templates.js";
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

  const getLineRequest = () => {
    return {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      bgColor: $subwayLineColorInput.value
    }
  };

  const onCreateSubwayLine = async () => {
    const lineRequest = getLineRequest();
    const lineResponse = await api.line.create(lineRequest);
    const responseLocation = lineResponse.headers.get("Location");
    if (lineResponse.ok) {
      const newSubwayLine = {
        id: responseLocation.split("/")[2],
        name: lineRequest.name,
        bgColor: lineRequest.bgColor
      };

      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(newSubwayLine)
      );

      subwayLineModal.toggle();
      $subwayLineNameInput.value = "";
      $subwayLineColorInput.value = "";
    } else {
      alert(lineResponse);
    }
  };

  const onUpdateSubwayLine = async ($lineItem) => {
    const lineId = $lineItem.dataset.lineId;
    const lineBgColor = $lineItem.dataset.bgColor;
    const lineRequest = getLineRequest();
    const lineResponse = await api.line.update(lineId, lineRequest);
    if (lineResponse.ok) {
      const newSubwayLine = {
        id: lineId,
        name: lineRequest.name,
        bgColor: lineRequest.bgColor
      };
      $lineItem.querySelector(`.${lineBgColor}`).classList.replace(lineBgColor,
        newSubwayLine.bgColor);
      $lineItem.querySelector(".line-name").innerText = newSubwayLine.name;

      subwayLineModal.removeUpdateFromClassList();
      subwayLineModal.toggle();
      $subwayLineNameInput.value = "";
      $subwayLineColorInput.value = "";
    } else {
      alert(lineResponse);
    }
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    event.preventDefault();
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $lineItem = $target.closest(".subway-line-item");
      const lineId = $lineItem.dataset.lineId;

      api.line.delete(lineId)
      .then(response => {
        if (response.ok) {
          $lineItem.remove();
        } else {
          alert(response);
        }
      });
    }
  };

  const onEditSubwayLine = async (event) => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      const $lineItem = $target.closest(".subway-line-item");
      subwayLineModal.toggle();
      subwayLineModal.addUpdateToClassList();
      subwayLineModal.addCurrentLineItem($lineItem);
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
      const lineId = $lineItem.dataset.lineId;
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

  const initSubwayLines = async () => {
    const initLines = await api.line.get();

    initLines.map(line => {
      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(line)
      );
    });
  };

  const isCreate = (event) => {
    const $target = event.target;
    const $modal = $target.closest(".modal");

    event.preventDefault();

    if ($modal.classList.contains("update")) {
      const $lineItem = subwayLineModal.getCurrentLineItem();
      return onUpdateSubwayLine($lineItem);
    }
    return onCreateSubwayLine();
  }

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onReadSubwayInfo);
    $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, isCreate);
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
