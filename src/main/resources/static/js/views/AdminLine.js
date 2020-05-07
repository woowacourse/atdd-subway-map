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

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

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
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
  };

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

  const onSelectSubwayLine = async event => {
    event.preventDefault();
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const isModifyButton = $target.classList.contains("mid-pencil");
    if (!isDeleteButton && !isModifyButton) {
      const lineName = event.target.innerText.trim();
      const selectedLine = subwayLines.find(subway => subway["name"] === lineName);
      document.querySelector("body > div.flex.justify-center.md\\:py-10.lg\\:py-10.app-container > div > div:nth-child(1) > div.lines-info.flex.flex-wrap.mb-3.w-full > div:nth-child(2)")
          .innerText = selectedLine["startTime"];
      document.querySelector("body > div.flex.justify-center.md\\:py-10.lg\\:py-10.app-container > div > div:nth-child(1) > div.lines-info.flex.flex-wrap.mb-3.w-full > div:nth-child(4)")
          .innerText = selectedLine["endTime"];
      document.querySelector("body > div.flex.justify-center.md\\:py-10.lg\\:py-10.app-container > div > div:nth-child(1) > div.lines-info.flex.flex-wrap.mb-3.w-full > div:nth-child(6)")
          .innerText = selectedLine["intervalTime"];
    }
  }

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-pencil");
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
