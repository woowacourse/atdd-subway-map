import { EVENT_TYPE } from "../../utils/constants.js";
import { colorSelectOptionTemplate, subwayLinesTemplate } from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  let lines = [];
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayFirstTimeInput = document.querySelector("#first-time");
  const $subwayLastTimeInput = document.querySelector("#last-time");
  const $subwayIntervalInput = document.querySelector("#interval-time");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayFirstTime = document.querySelector("#display-first-time");
  const $subwayLastTime = document.querySelector("#display-last-time");
  const $subwayInterval = document.querySelector("#display-interval-time");
  const $modalClose = document.querySelector(".modal-close");

  let edit = null;

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  const onCreateSubwayLine = event => {
    event.preventDefault();
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      startTime: $subwayFirstTimeInput.value,
      endTime: $subwayLastTimeInput.value,
      intervalTime: $subwayIntervalInput.value,
      bgColor: $subwayLineColorInput.value
    };
    if (edit == null) {
      createSubwayLine(newSubwayLine);
      return;
    }
    updateSubwayLine(newSubwayLine);
  };

  const createSubwayLine = async (newSubwayLine) => {
    const line = await api.line.create(newSubwayLine);
    lines = [...lines, line];
    $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate(line)
    );
    subwayLineModal.toggle();
    initInputValue();
  };

  const updateSubwayLine = async newSubwayLine => {
    const updatedLine = await api.line.update(edit, newSubwayLine);
    const standardNode = document.querySelector(`div[data-id="${edit}"]`);
    const divNode = document.createElement("div");
    divNode.innerHTML = subwayLinesTemplate(updatedLine);
    $subwayLineList.insertBefore(divNode.childNodes[0], standardNode);
    standardNode.remove();

    lines = lines.filter(line => line.name !== updatedLine.name);
    lines = [...lines, updatedLine];

    edit = null;
    subwayLineModal.toggle();
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const lineName = $target.closest('.subway-line-item').innerText.trim();
    if (isDeleteButton) {
      $target.closest(".subway-line-item").remove();
      const deleteLine = findLineByName(lineName);
      api.line.delete(deleteLine.id);
      lines = lines.filter(line => line.name !== lineName);
    }
  };

  const onShowSubwayLine = event => {
    const $target = event.target;
    const lineName = $target.closest('.subway-line-item').innerText.trim();
    const isSubwayLineItem = $target.classList.contains("subway-line-item");
    if (isSubwayLineItem) {
      const showLine = findLineByName(lineName);
      $subwayFirstTime.innerText = showLine.startTime;
      $subwayLastTime.innerText = showLine.endTime;
      $subwayInterval.innerText = `${showLine.intervalTime} 분`;
    }
  };

  const onEditSubwayLine = event => {
    const $target = event.target;
    const lineName = $target.closest('.subway-line-item').innerText.trim();
    const isEditButton = $target.classList.contains("mdi-pencil");

    if (isEditButton) {
      subwayLineModal.toggle();
      const updateLine = findLineByName(lineName);
      edit = updateLine.id;
      $subwayLineNameInput.value = updateLine.name;
      $subwayFirstTimeInput.value = updateLine.startTime;
      $subwayLastTimeInput.value = updateLine.endTime;
      $subwayIntervalInput.value = updateLine.intervalTime;
      $subwayLineColorInput.value = updateLine.bgColor;
    }
  };

  const findLineByName = (lineName) => {
    return lines.find(line => line.name === lineName)
  };

  const onInitInputValue = event => {
    event.preventDefault();
    initInputValue();
    edit = null;
  };

  const initInputValue = () => {
    $subwayLineNameInput.value = "";
    $subwayFirstTimeInput.value = "";
    $subwayLastTimeInput.value = "";
    $subwayIntervalInput.value = "";
    $subwayLineColorInput.value = "";
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onShowSubwayLine);
    $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
    $modalClose.addEventListener(EVENT_TYPE.CLICK, onInitInputValue)
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

  const showLines = async () => {
    const persistLines = await api.lines.get();
    lines = [...persistLines];
    persistLines.forEach(persistLine => $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate(persistLine)
    ));
  };

  this.init = () => {
    showLines();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
