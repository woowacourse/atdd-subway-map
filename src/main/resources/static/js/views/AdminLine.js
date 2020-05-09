import { EVENT_TYPE } from "../../utils/constants.js";
import {
  colorSelectOptionTemplate,
  subwayLineInfoTemplate,
  subwayLinesTemplate
} from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  let lines = [];
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineIdInput = document.querySelector("#subway-line-id");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $firstTimeInput = document.querySelector("#first-time");
  const $lastTimeInput = document.querySelector("#last-time");
  const $intervalTimeInput = document.querySelector("#interval-time");
  const $subwayLineInfo = document.querySelector("#subway-line-info");
  const $subwayLineName = document.querySelector("#subway-line-name");
  const $subwayFirstTime = document.querySelector("#first-time");
  const $subwayLastTime = document.querySelector("#last-time");
  const $subwayIntervalTime = document.querySelector("#interval-time");
  const $subwayLineColor = document.querySelector("#subway-line-color");
  const $subwayLineId = document.querySelector("#subway-line-id");
  const $openModalButton = document.querySelector(".modal-open");

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  const initializeData = (line = {}) => {
    $subwayLineId.value = line.id ? line.id : "";
    $subwayLineName.value = line.name ? line.name : "";
    $subwayFirstTime.value = line.startTime ? line.startTime : "";
    $subwayLastTime.value = line.endTime ? line.endTime : "";
    $subwayIntervalTime.value = line.intervalTime ? line.intervalTime : "";
    $subwayLineColor.value = line.color ? line.color : "";
  };

  const onSubmitSubwayLine = event => {
    event.preventDefault();
    const id = parseInt($subwayLineIdInput.value.trim());
    const newSubwayLine = {
      name: $subwayLineNameInput.value.trim(),
      color: $subwayLineColorInput.value.trim(),
      startTime: $firstTimeInput.value.trim(),
      endTime: $lastTimeInput.value.trim(),
      intervalTime: $intervalTimeInput.value.trim(),
    };
    id ? updateLine(newSubwayLine, id) : createLine(newSubwayLine);
  };

  const createLine = (newSubwayLine) => {
    api.line.create(newSubwayLine)
    .then(response => {
      if (response.status !== 201) {
        throw new Error("잘못된 요청입니다.");
      }
      return response.json();
    }).then(id => {
      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(newSubwayLine, id)
      );
      subwayLineModal.toggle(event);
      initializeData();
      newSubwayLine.id = id;
      lines = [...lines, newSubwayLine];
    }).catch(error => alert(error.message));
  }

  const updateLine = (newSubwayLine, id) => {
    api.line.update(newSubwayLine, id)
    .then(response => {
      if (response.status !== 200) {
        throw new Error("잘못된 요청입니다.");
      }
      newSubwayLine.id = id;
      lines = lines.map(line => line.id === id ? newSubwayLine : line)
      $subwayLineList.innerHTML = lines.map(line => subwayLinesTemplate(line, line.id)).join("");
      subwayLineModal.toggle(event);
      initializeData();
    }).catch(error => alert(error.message));
  }

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const id = $target.closest("div").id;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      api.line.delete(id)
      .then(response => {
        if (response.status !== 204) {
          throw new Error("삭제 실패");
        }
        $target.closest(".subway-line-item").remove();
        lines = lines.filter(line => line.id !== id)
      }).catch(error => alert(error.message))
      event.stopPropagation();
    }
  };

  const showUpdateModal = event => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      const id = parseInt($target.closest("div").id.trim());
      const line = lines.find(line => parseInt(line.id) === id);
      if (line) {
        initializeData(line);
        subwayLineModal.toggle(event);
        return
      }
      alert("잘못된 요청입니다");
    }
  };

  const onGetSubwayLineInfo = event => {
    const $target = event.target;
    const isSubwayLineItem = $target.classList.contains("subway-line-item");
    if (isSubwayLineItem) {
      const id = parseInt($target.closest("div").id);
      const line = lines.find(line => line.id === id);
      if (line) {
        $subwayLineInfo.innerHTML = subwayLineInfoTemplate(line);
        return
      }
      alert("잘못된 요청입니다");
    }
  }

  const initSubwayLines = async () => {
    api.line.getAll()
    .then(response => {
      if (response.status === 200) {
        return response.json();
      }
      throw new Error("잘못된 요청입니다.");
    }).then(fetchedLines => {
      fetchedLines.map(line => {
        $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(line, line.id)
        );
      })
      lines = [...fetchedLines];
    }).catch(error => alert(error.message));
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, showUpdateModal);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onGetSubwayLineInfo);
    $createSubwayLineButton.addEventListener(
      EVENT_TYPE.CLICK,
      onSubmitSubwayLine
    );
    $openModalButton.addEventListener(EVENT_TYPE.CLICK, initializeData);
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
