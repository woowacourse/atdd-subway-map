import { EVENT_TYPE } from "../../utils/constants.js";
import {
  colorSelectOptionTemplate,
  subwayLineInfoTemplate,
  subwayLinesTemplate
} from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import Api from "../../api/index.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $subwayLineInfoContainer = document.querySelector("#subway-line-info-container");

  const $subwayLineFormSubmitButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  let lineId = 0;

  const onCreateSubwayLine = event => {
    event.preventDefault();

    const lineRequest = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      color: $subwayLineColorInput.value
    };

    Api.line.create(lineRequest)
    .then((response) => response.json())
    .then((data) => {
      const selectedSubwayLine = {
        id: data.id,
        name: data.name,
        color: data.color
      };

      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(selectedSubwayLine)
      );

      const selectedSubwayInfo = {
        startTime: data.startTime,
        endTime: data.endTime,
        intervalTime: data.intervalTime
      };

      $subwayLineInfoContainer.innerHTML = subwayLineInfoTemplate(selectedSubwayInfo);
    })
    .then(() => {
      subwayLineModal.toggle();
      $subwayLineNameInput.value = "";
      $subwayLineColorInput.value = "";
      $subwayLineFirstTimeInput.value = "";
      $subwayLineLastTimeInput.value = "";
      $subwayLineIntervalTimeInput.value = "";
    });
  };

  const onReadSubwayLine = event => {
    const $target = event.target;
    const isNameDiv = $target.classList.contains("line-name");
    if (isNameDiv) {
      const lineId = $target.closest(".subway-line-item").querySelector(".line-id").textContent;
      Api.line.getDetail(lineId)
      .then((response) => response.json())
      .then((data) => {
        const selectedSubwayInfo = {
          startTime: data.startTime,
          endTime: data.endTime,
          intervalTime: data.intervalTime
        };
        $subwayLineInfoContainer.innerHTML = subwayLineInfoTemplate(selectedSubwayInfo);
      })
    }
  };

  const onUpdateSubwayLine = event => {
    const $target = event.target;

    const lineRequest = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      color: $subwayLineColorInput.value
    };

    Api.line.update(lineRequest, lineId)
    .then((response) => response.json())
    .then((data) => {
      const selectedSubwayLine = {
        id: data.id,
        name: data.name,
        color: data.color
      };

      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(selectedSubwayLine)
      );

      const selectedSubwayInfo = {
        startTime: data.startTime,
        endTime: data.endTime,
        intervalTime: data.intervalTime
      };
      $subwayLineInfoContainer.innerHTML = subwayLineInfoTemplate(selectedSubwayInfo);
      $subwayLineFormSubmitButton.removeEventListener(
        EVENT_TYPE.CLICK,
        onEditSubwayLine
      );
      $subwayLineFormSubmitButton.addEventListener(
        EVENT_TYPE.CLICK,
        onCreateSubwayLine
      );


      subwayLineModal.toggle();
    })
  }
};

const onDeleteSubwayLine = event => {
  const $target = event.target;
  const isDeleteButton = $target.classList.contains("mdi-delete");
  if (isDeleteButton) {
    const lineId = $target.closest(".subway-line-item").querySelector(".line-id").textContent;
    Api.line.delete(lineId);
    $target.closest(".subway-line-item").remove();
  }
};

const onShowUpdateSubwayLineModal = event => {
  const $target = event.target;
  const isUpdateButton = $target.classList.add("update-button");
  if (isUpdateButton) {
  }
};

const initDefaultSubwayLines = () => {
    Api.line.get()
    .then((response) => response.json())
    .then((data) => {
      data.map(line => {
        $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(line));
      })
    })
  }
;

const initEventListeners = () => {
  $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
  $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onReadSubwayLine);
  $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onShowUpdateSubwayLineModal);
  $subwayLineFormSubmitButton.addEventListener(EVENT_TYPE.CLICK, onSubmitHandler);
};

const onSubmitHandler = event => {
  event.preventDefault();
  const $target = event.target;
  const isUpdateSubmit = $target.classList.contains("update-button");
  isUpdateSubmit ? onUpdateSubwayLine($target) : onCreateSubwayLine($target);
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
  .map((option, index) => colorSelectOptionTemplate(option, index)
  )
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
}

const adminLine = new AdminLine();
adminLine.init();
