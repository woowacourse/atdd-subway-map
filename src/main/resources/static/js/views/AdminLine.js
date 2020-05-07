import { EVENT_TYPE } from "../../utils/constants.js";
import {
  colorSelectOptionTemplate,
  subwayLineInfoTemplate,
  subwayLinesTemplate
} from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $firstTimeInput = document.querySelector("#first-time");
  const $lastTimeInput = document.querySelector("#last-time");
  const $intervalTimeInput = document.querySelector("#interval-time");
  const $subwayLineInfo = document.querySelector("#subway-line-info");

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  const onCreateSubwayLine = event => {
    event.preventDefault();
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      color: $subwayLineColorInput.value,
      startTime: $firstTimeInput.value,
      endTime: $lastTimeInput.value,
      intervalTime: $intervalTimeInput.value,
    };
    const lineRequest = {
      method: "POST",
      headers: {
        'Content-Type': "application/json",
      },
      body: JSON.stringify(newSubwayLine),
    }
    fetch("/lines", lineRequest)
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
      subwayLineModal.toggle();
      $subwayLineNameInput.value = "";
      $subwayLineColorInput.value = "";
      $firstTimeInput.value = "";
      $lastTimeInput.value = "";
      $intervalTimeInput.value = "";
    }).catch(error => {
      alert(error.message);
    })
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

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-pencil");
  };

  const onGetSubwayLineInfo = event => {
    const $target = event.target;
    const id = $target.closest("div").id;
    fetch(`/lines/${id}`)
    .then(response => {
      if (response.status === 200) {
        return response.json();
      }
      throw new Error("잘못된 노선 번호입니다.");
    }).then(line => {
      $subwayLineInfo.innerHTML = subwayLineInfoTemplate(line);
    }).catch(error => alert(error.message))
  }

  const initSubwayLines = async () => {
    const response = await fetch("/lines");
    const lines = await response.json();
    lines.map(line => {
      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(line, line.id)
      );
    });
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onGetSubwayLineInfo);
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
    initSubwayLines();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
