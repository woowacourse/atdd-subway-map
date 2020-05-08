import { EVENT_TYPE } from "../../utils/constants.js";
import {
  subwayLinesTemplate,
  colorSelectOptionTemplate
} from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import { api } from "../../api/index.js"

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineStartTimeInput = document.querySelector("#first-time");
  const $subwayLineEndTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $openModalButton = document.querySelector(".modal-open");

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  const onToggleModalForCreate = event => {
    $createSubwayLineButton.classList.remove("update")
    $createSubwayLineButton.classList.add("create")
    subwayLineModal.toggle()
  }

  const onToggleModalForUpdate = event => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      subwayLineModal.toggle();
    }
    $createSubwayLineButton.dataset.lineId = $target.closest(".subway-line-item").dataset.lineId
    $createSubwayLineButton.classList.remove("create")
    $createSubwayLineButton.classList.add("update")
  }

  const onSubmitButton = event => {
    event.preventDefault();
    if ($createSubwayLineButton.classList.contains("create")) {
      onCreateSubwayLine(event)
    }

    if ($createSubwayLineButton.classList.contains("update")) {
      onUpdateSubwayLine(event)
    }
  }

  const onCreateSubwayLine = async event => {
    let data = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineStartTimeInput.value,
      endTime: $subwayLineEndTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      color: $subwayLineColorInput.value
    }
    const response = await api.line.create(data);
    console.log(response);
    $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(response)
    );
    subwayLineModal.toggle();
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
  };


  const onUpdateSubwayLine = event => {
    let data = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineStartTimeInput.value,
      endTime: $subwayLineEndTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      color: $subwayLineColorInput.value
    }
    const id = $createSubwayLineButton.dataset.lineId
    api.line.update(id, data);
    subwayLineModal.toggle();
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
    const lines = Array.from($subwayLineList.childNodes)
    data.id = id;
    lines.find(line => line.dataset.lineId === id).remove()
    $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(data)
    );
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      api.line.delete($target.parentElement.parentElement.dataset.lineId)
      $target.closest(".subway-line-item").remove();
    }
  };

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-pencil");
  };

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
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onToggleModalForUpdate);
    $openModalButton.addEventListener(EVENT_TYPE.CLICK, onToggleModalForCreate);
    $createSubwayLineButton.addEventListener(
      EVENT_TYPE.CLICK,
        onSubmitButton
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

  const initLine = async () => {
    const lines =  await api.line.get();
    console.log(lines)
    lines.forEach(line => {
      $subwayLineList.insertAdjacentHTML("beforeend", subwayLinesTemplate(line))
    });
  };

  this.init = () => {
    // initDefaultSubwayLines();
    initLine();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
