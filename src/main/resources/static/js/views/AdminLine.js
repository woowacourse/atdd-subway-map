import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineStartTimeInput = document.querySelector("#first-time");
  const $subwayLineEndTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalInput = document.querySelector("#interval-time");
  let subwayLines = [];
  let selectedSubwayId = null;

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  function componentClear() {
    $subwayLineNameInput.value = "";
    $subwayLineStartTimeInput.value = "";
    $subwayLineEndTimeInput.value = "";
    $subwayLineColorInput.value = "";
  }

  const onCreateSubwayLine = async event => {
    event.preventDefault();
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      bgColor: $subwayLineColorInput.value,
      startTime: $subwayLineStartTimeInput.value,
      endTime: $subwayLineEndTimeInput.value,
      intervalTime: $subwayLineIntervalInput.value
    };
    const savedLine = await api.line.create(newSubwayLine);
    subwayLines = [...subwayLines, savedLine];
    $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate(newSubwayLine)
    );
    subwayLineModal.toggle();
    componentClear();
  };

  const onDeleteSubwayLine = async event => {
    event.preventDefault();
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".subway-line-item").remove();
      const lineName = event.target.parentNode.parentNode.innerText.trim();
      const selectedLineId = subwayLines.find(subway => subway["name"] === lineName)["id"];
      await api.line.delete(selectedLineId);
      let index = 0;
      for (let i = 0; i < subwayLines.length; i++) {
        if (subwayLines[i]["id"] === selectedLineId) {
          index = i;
          break;
        }
      }
      subwayLines.splice(index, 1);
    }
  };

  const onUpdateSubwayLine = async event => {
    const updatedLine = {
      name: $subwayLineNameInput.value,
      bgColor: $subwayLineColorInput.value,
      startTime: $subwayLineStartTimeInput.value,
      endTime: $subwayLineEndTimeInput.value,
      intervalTime: $subwayLineIntervalInput.value
    };
    await api.line.update(updatedLine, selectedSubwayId);
    let index = 0;
    for (let i = 0; i < subwayLines.length; i++) {
      if (subwayLines[i]["id"] === selectedSubwayId) {
        index = i;
        break;
      }
    }
    subwayLines.splice(index, 1, updatedLine[selectedSubwayId]);
    subwayLineModal.toggle();
    componentClear();
    changeEvent();
  };

  const onSelectSubwayLine = event => {
    event.preventDefault();
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const isModifyButton = $target.classList.contains("mid-pencil");
    if ($target && !isDeleteButton && !isModifyButton) {
      const lineName = event.target.innerText.trim();
      const selectedLine = subwayLines.find(subway => subway["name"] === lineName);
      const detailDiv = document.querySelectorAll(".lines-info > div");
      detailDiv[1].innerText = selectedLine["startTime"];
      detailDiv[3].innerText = selectedLine["endTime"];
      detailDiv[5].innerText = selectedLine["intervalTime"];
    }
  }

  function changeEvent() {
    if ($subwayLineNameInput.value === "") {
      $createSubwayLineButton.removeEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
      $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
      return;
    }
    $createSubwayLineButton.removeEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
    $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
  }

  const onEditSubwayLine = event => {
    event.preventDefault();
    const $target = event.target;
    const isModifyButton = $target.classList.contains("mdi-pencil");
    if ($target && isModifyButton) {
      const lineName = event.target.parentNode.parentNode.innerText.trim();
      const selectedLine = subwayLines.find(subway => subway["name"] === lineName);
      $subwayLineNameInput.value = selectedLine["name"];
      $subwayLineStartTimeInput.value = selectedLine["startTime"];
      $subwayLineEndTimeInput.value = selectedLine["endTime"];
      $subwayLineIntervalInput.value = selectedLine["intervalTime"];
      $subwayLineColorInput.value = selectedLine["bgColor"];
      selectedSubwayId = selectedLine["id"];
      changeEvent();
      subwayLineModal.toggle();
    }
  };

  const initDefaultSubwayLines = async () => {
    subwayLines = await api.line.get();
    subwayLines.map(line => {
      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(line)
      );
    });
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLine);
    changeEvent();
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
