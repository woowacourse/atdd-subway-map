import { EVENT_TYPE } from "../../utils/constants.js";
import {
  colorSelectOptionTemplate,
  subwayLineDetailTemplate,
  subwayLinesInnerTemplate,
  subwayLinesTemplate
} from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminLine() {
  let lines = [];
  let modifyingLineId = null;

  const $subwayLinesInfo = document.querySelector(".lines-info");
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineStartTimeInput = document.querySelector("#first-time");
  const $subwayLineEndTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");
  const $createSubwayLineButton = document.querySelector("#subway-line-create-form #submit-button");
  const subwayLineModal = new Modal();

  const validate = (name, startTime, endTime, intervalTime, bgColor) => {
    if (name === null || name.length === 0) {
      alert("호선명은 빈 칸이 될 수 없습니다.");
      return false;
    }

    const timeRegex = /^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?$/;
    const colorRegex = /^bg-[\w]+-[\d]+$/;

    if (!(timeRegex.test(startTime) && timeRegex.test(endTime))) {
      alert("올바른 형식의 시간이 아닙니다. (HH:MM 형식으로 입력해주세요.)");
      return false;
    }

    if (intervalTime <= 0 || isNaN(intervalTime)) {
      alert("올바른 형식의 간격이 아닙니다. (0보다 큰 숫자로 입력해주세요.)");
      return false;
    }

    if (!(colorRegex.test(bgColor))) {
      alert("올바른 형식의 색이 아닙니다.");
      return false;
    }
    return true;
  };

  const onCreateSubwayLine = event => {
    event.preventDefault();
    const name = $subwayLineNameInput.value.trim();
    const startTime = $subwayLineStartTimeInput.value.trim();
    const endTime = $subwayLineEndTimeInput.value.trim();
    const intervalTime = $subwayLineIntervalTimeInput.value.trim();
    const bgColor = $subwayLineColorInput.value.trim();
    let id;

    if (!validate(name, startTime, endTime, intervalTime, bgColor)) {
      return;
    }

    const updateLine = () => {
      api.line.update({ name, startTime, endTime, intervalTime, bgColor }, modifyingLineId)
        .then(response => {
          lines = lines.map(line => line.id === modifyingLineId ? response : line);
          const selected = document.querySelector(`#line-${modifyingLineId}`);
          selected.innerHTML = subwayLinesInnerTemplate({
            title: response.name,
            bgColor: response.bgColor
          });
        })
        .catch(error => {
          console.log(error);
        })
        .finally(() => {
          modifyingLineId = null;
        });
    };

    const createLine = () => {
      api.line.create({ name, bgColor, startTime, endTime, intervalTime })
        .then(response => {
          lines = [...lines, response];
          id = response.id;
        });

      $subwayLineList.insertAdjacentHTML("beforeend",
        subwayLinesTemplate({ title: name, bgColor, id }));
    };

    if (lines.some(line => line.id === modifyingLineId)) {
      updateLine();
    } else {
      createLine();
    }

    subwayLineModal.toggle();
  };

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      modifyingLineId = parseInt($target.closest(".subway-line-item").dataset.lineId);
      api.line.delete(modifyingLineId)
        .then(() => {
          lines = lines.filter(line => line.id !== modifyingLineId);
        })
        .catch(err => {
          console.log(err);
        })
        .finally(() => {
          modifyingLineId = null;
        });
      $target.closest(".subway-line-item").remove();
    }
  };

  const onUpdateSubwayLine = event => {
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      modifyingLineId = parseInt(event.target.closest(".subway-line-item").dataset.lineId);
      subwayLineModal.toggle();
      api.line.get(modifyingLineId)
        .then(data => {
          $subwayLineNameInput.value = data.name;
          $subwayLineStartTimeInput.value = data.startTime;
          $subwayLineEndTimeInput.value = data.endTime;
          $subwayLineIntervalTimeInput.value = data.intervalTime;
          $subwayLineColorInput.value = data.bgColor;
        });
    }
  };

  const onReadSubwayLine = event => {
    const targetElement = event.target;
    if (targetElement.classList.contains("subway-line-item")) {
      const lineId = parseInt(targetElement.dataset.lineId);
      const line = lines.find(line => line.id === lineId);
      $subwayLinesInfo.innerHTML = subwayLineDetailTemplate(line);
    }
  };

  const initDefaultSubwayLines = () => {
    api.line.getAll()
      .then(data => {
        lines = data;
        lines
          .map(({ name, bgColor, id }) => ({ title: name, bgColor, id }))
          .map(line => {
            $subwayLineList.insertAdjacentHTML("beforeend", subwayLinesTemplate(line));
          });
      });
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onReadSubwayLine);
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
    $colorSelectContainer.addEventListener(EVENT_TYPE.CLICK, onSelectColorHandler);
  };

  this.init = () => {
    initDefaultSubwayLines();
    initEventListeners();
    initCreateSubwayLineForm();
  };
}

const adminLine = new AdminLine();
adminLine.init();
