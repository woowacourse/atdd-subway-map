import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );

  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const subwayLineModal = new Modal();

  const onCreateSubwayLine = event => {
    event.preventDefault();
    saveLine();
  };

  function saveLine() {
    fetch("/lines", {
      method: "POST",
      body: JSON.stringify({
        name: $subwayLineNameInput.value,
        startTime: $subwayLineFirstTimeInput.value,
        endTime: $subwayLineLastTimeInput.value,
        intervalTime: $subwayLineIntervalTimeInput.value,
        lineColor: $subwayLineColorInput.value
      }),
      headers: {"Content-Type": "application/json"}
    })
      .then(response => response.json())
      .then(data => addLineInView(data));
  }

  function addLineInView(data) {
    const newSubwayLine = {
      title: data.name,
      bgColor: data.lineColor
    };
    $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate(newSubwayLine)
    );
    subwayLineModal.toggle();
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
  }

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".subway-line-item").remove();
    }
  };

  const onUpdateSubwayLine = event => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      subwayLineModal.toggle();
    }
  };

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

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
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
    initDefaultSubwayLines();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
