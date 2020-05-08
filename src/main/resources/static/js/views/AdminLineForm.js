import { EVENT_TYPE } from "../../utils/constants.js";
import {
  subwayLinesTemplate,
  submitButtonTemplate,
  colorSelectOptionTemplate
} from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineStartTime = document.querySelector("#subway-start-time");
  const $subwayLineEndTime = document.querySelector("#subway-end-time");
  const $subwayIntervalTime = document.querySelector("#subway-interval-time");
  const $subwayLineFormSubmitButton = document.querySelector("#submit-button");
  let $activeSubwayLineItem = null

  const subwayLineModal = new Modal();

  const createSubwayLine = () => {
    const newSubwayLine = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineStartTime.value,
      endTime: $subwayLineEndTime.value,
      intervalTime: $subwayIntervalTime.value
      // color: $subwayLineColorInput.value
    };
    api.line
      .create(newSubwayLine)
      .then(response => {
        $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(response)
        );
        subwayLineModal.toggle();
      })
      .catch(error => {
        alert("에러가 발생했습니다.");
      });
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const $subwayLineItem = $target.closest(".subway-line-item")
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (!isDeleteButton) {
      return
    }
    const lineId = $subwayLineItem.dataset.lineId
    api.line.delete(lineId).then(() => {
      $subwayLineItem.remove();
    }).catch((error) => {
      alert(error)
    })
  };

  const updateSubwayLine = () => {
    const updatedSubwayLine = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineStartTime.value,
      endTime: $subwayLineEndTime.value,
      intervalTime: $subwayIntervalTime.value
    };
    api.line.update($activeSubwayLineItem.dataset.lineId, updatedSubwayLine).then((line) => {
      subwayLineModal.toggle();
    }).catch(() => {
      alert('업데이트에 실패했습니다.')
    })
  };

  const onShowUpdateSubwayLineModal = event => {
    const $target = event.target;
    const $subwayLineItem = $target.closest(".subway-line-item")
    $activeSubwayLineItem = $subwayLineItem
    const $submitButton =  document.querySelector('#submit-button')
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (!isUpdateButton) {
      return
    }
    const lineId = $subwayLineItem.dataset.lineId
    api.line.get(lineId).then((line) => {
      $subwayLineNameInput.value = line.name
      $subwayLineStartTime.value = line.startTime
      $subwayLineEndTime.value = line.endTime
      $subwayIntervalTime.value = line.intervalTime
      subwayLineModal.toggle();
      $submitButton.classList.add('update-submit-button')
    }).catch(() => {
      alert('데이터를 불러올 수 없습니다.')
    })
  }

  const onSubmitHandler = (event) => {
    event.preventDefault()
    const $target = event.target;
    const isUpdateSubmit = $target.classList.contains("update-submit-button");
    isUpdateSubmit ? updateSubwayLine($target) : createSubwayLine()
  }

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
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onShowUpdateSubwayLineModal);
    // $subwayLineFormSubmitButton.addEventListener(
    //   EVENT_TYPE.CLICK,
    //   onSubmitHandler
    // );
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
