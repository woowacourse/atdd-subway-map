import { EVENT_TYPE } from "../../utils/constants.js";
import { colorSelectOptionTemplate, subwayLinesTemplate } from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  let isUpdate = false;

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
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      bgColor: $subwayLineColorInput.value,
      startTime: $subwayLineFirstTimeInput.value + ":00",
      endTime: $subwayLineLastTimeInput.value + ":00",
      intervalTime: $subwayLineIntervalTimeInput.value
    };

    if (!isUpdate) {
      let newLine = await api.line.create(newSubwayLine);

      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(newLine)
      );
      $subwayLineNameInput.value = "";
      $subwayLineColorInput.value = "";
    } else {
      const id = document.querySelector(".modal").dataset.sourceId;
      await api.line.update(id, newSubwayLine);
      await initDefaultSubwayLines();
    }
    isUpdate = false;
    subwayLineModal.toggle();
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".subway-line-item").remove();
    }
  };

  const onUpdateSubwayLine = async (event) => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      const id = $target.closest(".subway-line-item").id;
      const line = await api.line.findById(id);
      document.querySelector("#subway-line-name").value = line.title;
      document.querySelector("#first-time").value = line.startTime.substr(0, 5);
      document.querySelector("#last-time").value = line.endTime.substr(0, 5);
      document.querySelector("#interval-time").value = line.intervalTime;
      document.querySelector("#subway-line-color").value = line.bgColor;

      document.querySelector(".modal").dataset.sourceId = id;

      isUpdate = true;
      subwayLineModal.toggle();
    }
  };

  const onDetailSubwayLine = async (event) => {
    const $target = event.target;
    const isName = $target.classList.contains("name");
    if (isName) {
      const id = $target.closest(".subway-line-item").id;
      const lineDetail = await api.line.findById(id);
      document.querySelector(".lines-info").innerHTML = `
      <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">첫차 시간</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${lineDetail.startTime}</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">막차 시간</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${lineDetail.endTime}</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-200">간격</div>
        <div class="w-1/2 p-2 text-center text-gray-800 bg-gray-100">${lineDetail.intervalTime}</div>
      `;
    }
  }

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-pencil");
  };

  const initDefaultSubwayLines = async () => {
    $subwayLineList.innerHTML = '';

    const lines = await api.line.get();
    lines.map(line => {
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
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDetailSubwayLine);
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
