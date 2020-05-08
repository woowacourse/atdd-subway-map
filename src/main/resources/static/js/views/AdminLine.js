import {EVENT_TYPE} from "../../utils/constants.js";
import {colorSelectOptionTemplate, subwayLinesTemplate} from "../../utils/templates.js";
import {subwayLineColorOptions} from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

const UPDATE_BUTTON_CLASS_NAME = "mdi-pencil";
const DELETE_BUTTON_CLASS_NAME = "mdi-delete";

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

  let updatingId = 0;

  const onCreateSubwayLine = event => {
    event.preventDefault();
    if (validInput()) {
      saveOrUpdateLine();
    }
  };

  function validInput() {
    if ($subwayLineNameInput.value === "" ||
      $subwayLineFirstTimeInput.value === "" ||
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

  function saveOrUpdateLine() {
    if (updatingId) {
      updateLine();
      updatingId = 0;
      return;
    }
    let savedLine = saveLine();
    savedLine.then(data => addLineInView(data));
  }

  function updateLine() {
    fetch(`/lines/${updatingId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        name: $subwayLineNameInput.value,
        startTime: $subwayLineFirstTimeInput.value,
        endTime: $subwayLineLastTimeInput.value,
        intervalTime: $subwayLineIntervalTimeInput.value,
        lineColor: $subwayLineColorInput.value
      })
    }).then(data => closeAndResetModalValue());
  }

  function saveLine() {
    return api.line.create({
      name: $subwayLineNameInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      lineColor: $subwayLineColorInput.value
    });
  }

  function closeAndResetModalValue() {
    subwayLineModal.toggle();
    $subwayLineNameInput.value = "";
    $subwayLineFirstTimeInput.value = "";
    $subwayLineLastTimeInput.value = "";
    $subwayLineIntervalTimeInput.value = "";
    $subwayLineColorInput.value = "";
  }

  function addLineInView(data) {
    const newSubwayLine = {
      id: data.id,
      name: data.name,
      lineColor: data.lineColor
    };
    $subwayLineList.insertAdjacentHTML(
      "beforeend",
      subwayLinesTemplate(newSubwayLine)
    );
    addClickListeners();
    closeAndResetModalValue();
  }

  function addClickListeners() {
    $subwayLineList.lastChild.addEventListener(
      EVENT_TYPE.CLICK,
      onReadSubwayLine
    );
    $subwayLineList.lastChild.querySelector(`.${DELETE_BUTTON_CLASS_NAME}`).addEventListener(
      EVENT_TYPE.CLICK,
      onDeleteSubwayLine
    );
    $subwayLineList.lastChild.querySelector(`.${UPDATE_BUTTON_CLASS_NAME}`).addEventListener(
      EVENT_TYPE.CLICK,
      onUpdateSubwayLine
    );
  }

  const onReadSubwayLine = event => {
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

  // todo 이렇게 바꿔보자.
  // const showLineDetail = (data) => {
  //   $startTimeContainer.innerHTML = data.startTime;
  //   $endTimeContainer.innerHTML = data.endTime;
  //   $intervalTimeContainer.innerHTML = data.intervalTime;
  // };

  const onDeleteSubwayLine = event => {
    event.stopPropagation();
    const $target = event.target;
    const lineId = $target.closest(".subway-line-item").firstElementChild.id;

    const isDeleteButton = $target.classList.contains(DELETE_BUTTON_CLASS_NAME);
    if (isDeleteButton) {
      fetch(`/lines/${lineId}`, {
        method: "DELETE"
      });
      $target.closest(".subway-line-item").remove();
    }
  };

  const onUpdateSubwayLine = event => {
    event.stopPropagation();
    const $target = event.target;
    const lineId = $target.closest(".subway-line-item").firstElementChild.id;

    const isUpdateButton = $target.classList.contains(UPDATE_BUTTON_CLASS_NAME);
    if (isUpdateButton) {
      api.line.getBy(lineId)
        .then(data => {
          $subwayLineNameInput.value = data.name;
          $subwayLineFirstTimeInput.value = data.startTime;
          $subwayLineLastTimeInput.value = data.endTime;
          $subwayLineIntervalTimeInput.value = data.intervalTime;
          $subwayLineColorInput.value = data.lineColor;
          updatingId = lineId;
        });
      closeAndResetModalValue();
    }
  };

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains(UPDATE_BUTTON_CLASS_NAME);
  };

  const initDefaultSubwayLines = () => { // 이벤트 위임으로 처리하셨다.
    api.line.get()
      .then(data => {
        if (data.length === 0) {
          return;
        }
        data.forEach(line =>
          $subwayLineList.insertAdjacentHTML(
            "beforeend",
            subwayLinesTemplate(line)
          )
        )
        ;
      });
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
    $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
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
