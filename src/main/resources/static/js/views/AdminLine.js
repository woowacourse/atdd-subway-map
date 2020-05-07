import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $createSubwayLineButton = document.querySelector("#subway-line-create-form #submit-button");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const subwayLineModal = new Modal();
  const $startTimeContainer = document.querySelector("#start-time-container");
  const $endTimeContainer = document.querySelector("#end-time-container");
  const $intervalTimeContainer = document.querySelector("#interval-time-container");

  const onCreateSubwayLine = event => {
    event.preventDefault();
    if (validInput()) {
      saveLine();
    }
  };

  function validInput() {
    if ($subwayLineNameInput.value === "" || $subwayLineFirstTimeInput.value === "" ||
      $subwayLineLastTimeInput.value === "" ||
      $subwayLineColorInput.value === "") {
      alert("값을 입력해야합니다.");
      return false;
    }

    // todo 시간 유효성 검사

    if (!/\d/.test($subwayLineIntervalTimeInput.value)) {
      alert("숫자만 입력해주세요");
      return false;
    }

    if ($subwayLineIntervalTimeInput.value <= 0) {
      alert("양수만 입력해주세요.");
      return false;
    }

    return true;
  }

  function saveLine() {
    api.line.create({
      name: $subwayLineNameInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      lineColor: $subwayLineColorInput.value
    }).then(data => addLineInView(data));
  }

  function addLineInView(data) {
    const newSubwayLine = {
      id: data.id,
      title: data.name,
      bgColor: data.lineColor
    };
    $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate(newSubwayLine)
    );
    $subwayLineList.lastChild.addEventListener(
      EVENT_TYPE.CLICK,
      onReadSubwayLine
    );
    subwayLineModal.toggle();
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
  }

  const onReadSubwayLine = event => {
    event.preventDefault();
    selectLine(event);
  };

  function selectLine(event) {
    const lineId = event.currentTarget.firstElementChild.id;

    api.line
      .getBy(lineId)
      .then(data => showLineDetail(data));
  }

  function showLineDetail(data) {
    $startTimeContainer.innerHTML = data.startTime;
    $endTimeContainer.innerHTML = data.endTime;
    $intervalTimeContainer.innerHTML = data.intervalTime;
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
    // defaultSubwayLines.map(line => {
    //   $subwayLineList.insertAdjacentHTML(
    //     "beforeend",
    //     subwayLinesTemplate(line)
    //   );
    // });
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
    // initDefaultSubwayLines();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
