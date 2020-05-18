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

  const $createSubwayLineAddButton = document.querySelector("#subway-line-add-btn");
  const $createSubwayLineFormSubmitButton = document.querySelector("#subway-line-create-form #submit-button");
  const subwayLineModal = new Modal();

  const onCreateLineFormInitHandler = () => {
    $subwayLineNameInput.value = "";
    $subwayLineStartTimeInput.value = "";
    $subwayLineEndTimeInput.value = "";
    $subwayLineColorInput.value = "";
    $subwayLineIntervalInput.value = "";
  }

  const onCreateSubwayLineHandler = async event => {
    event.preventDefault();
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      backgroundColor: $subwayLineColorInput.value,
      startTime: $subwayLineStartTimeInput.value,
      endTime: $subwayLineEndTimeInput.value,
      intervalTime: $subwayLineIntervalInput.value
    };
    try {
      const savedLine = await api.line.create(newSubwayLine);
      subwayLines = [...subwayLines, savedLine];
      $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(newSubwayLine)
      );
    } catch (e) {
      alert(e.message);
    }
    subwayLineModal.toggle();
  };

  const onDeleteSubwayLineHandler = async event => {
    event.preventDefault();
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".subway-line-item").remove();
      const lineName = event.target.parentNode.parentNode.innerText.trim();
      const selectedLineId = subwayLines.find(subway => subway["name"] === lineName)["id"];
      await api.line.delete(selectedLineId);
      const index = subwayLines.map(subway => subway["id"])
          .indexOf(selectedLineId);
      subwayLines.splice(index, 1);
    }
  };

  const onUpdateSubwayLineHandler = async event => {
    event.preventDefault();
    const updatedLine = {
      name: $subwayLineNameInput.value,
      backgroundColor: $subwayLineColorInput.value,
      startTime: $subwayLineStartTimeInput.value,
      endTime: $subwayLineEndTimeInput.value,
      intervalTime: $subwayLineIntervalInput.value
    };
    try {
      await api.line.update(updatedLine, selectedSubwayId);
      const index = subwayLines.map(subway => subway["id"])
          .indexOf(selectedSubwayId);
      subwayLines.splice(index, 1, updatedLine[selectedSubwayId]);
      subwayLineModal.toggle();
      changeEvent();
    } catch (e) {
      alert(e);
    }
  }

  const onSelectSubwayLineHandler = event => {
    event.preventDefault();
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const isModifyButton = $target.classList.contains("mdi-pencil");
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
      $createSubwayLineFormSubmitButton.removeEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLineHandler);
      $createSubwayLineFormSubmitButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLineHandler);
      return;
    }
    $createSubwayLineFormSubmitButton.removeEventListener(EVENT_TYPE.CLICK, onCreateSubwayLineHandler);
    $createSubwayLineFormSubmitButton.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLineHandler);
  }

  const onEditSubwayLineHandler = event => {
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
      $subwayLineColorInput.value = selectedLine["backgroundColor"];
      selectedSubwayId = selectedLine["id"];
      changeEvent();
      subwayLineModal.toggle();
    }
  };

  const initDefaultSubwayLines = async () => {
    subwayLines = await api.line.get();
    subwayLines.map(line => $subwayLineList.insertAdjacentHTML("beforeend", subwayLinesTemplate(line)));
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onSelectSubwayLineHandler);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLineHandler);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onEditSubwayLineHandler);
    $createSubwayLineAddButton.addEventListener(EVENT_TYPE.CLICK, onCreateLineFormInitHandler);
    changeEvent();
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

  this.init = () => {
    initDefaultSubwayLines().then();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
