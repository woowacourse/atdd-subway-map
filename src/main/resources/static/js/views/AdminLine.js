import { EVENT_TYPE } from "../../utils/constants.js";
import { colorSelectOptionTemplate, subwayLinesTemplate } from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import api from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminLine() {
  const $subwayLineList = document.querySelector("#subway-line-list");
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );
  const subwayLineModal = new Modal();

  const onCreateSubwayLine = event => {
    event.preventDefault();
    const data = {
      name: $subwayLineNameInput.value,
      bgColor: $subwayLineColorInput.value,
      firstTime: $subwayLineFirstTimeInput.value + ":00",
      lastTime: $subwayLineLastTimeInput.value + ":00",
      intervalTime: $subwayLineIntervalTimeInput.value
    };
    console.log("#1" + data);
    const $id = document.querySelector('#modal-update-condition').getAttribute("value");
    if ($id !== "") {
      console.log("#2" + data);
      api.line.update(data, $id).then();
    } else {
      api.line.create(data).then(data => {
        $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(data)
        );
        subwayLineModal.toggle();
        $subwayLineNameInput.value = "";
        $subwayLineColorInput.value = "";
        $subwayLineFirstTimeInput.value = "";
        $subwayLineLastTimeInput.value = "";
        $subwayLineIntervalTimeInput.value = "";
      });
    }
    const v = document.querySelector(".mdi mdi-pencil");
    v.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
  };

  const onDeleteSubwayLine = event => {
    event.stopPropagation();
    event.preventDefault();
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      api.line.delete($target.closest(".subway-line-item").getAttribute("value")).then(() => {
        $target.closest(".subway-line-item").remove();
      })
    }
  };

  const onUpdateSubwayLine = event => {
    event.stopPropagation();
    alert("");
    const $target = event.target;
    const $id = $target.closest('.subway-line-item').getAttribute("value");

    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      event.stopPropagation();
      event.preventDefault();
      document.querySelector('#modal-update-condition').value = $id;
      api.line.getOneLine($id).then(data => {
        document.querySelector('#subway-line-name').value = data.name;
        document.querySelector('#first-time').value = data.startTime.substring(0, 5);
        document.querySelector('#last-time').value = data.endTime.substring(0, 5);
        document.querySelector('#interval-time').value = data.intervalTime;
        document.querySelector('#subway-line-color').value = data.bgColor;
      });
      subwayLineModal.toggle();
    }
  };

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-pencil");
  };

  /* 처음 지하철 정보들 뿌려주는 */
  const initDefaultSubwayLines = () => {
    api.line.get().then(data => data.map(line => {
        $subwayLineList.insertAdjacentHTML(
          "beforeend",
          subwayLinesTemplate(line)
        );
      })
    )
  };

  const onReadDetailedInfo = event => {
    event.stopPropagation();
    event.preventDefault();
    const $id = event.target.getAttribute("value");
    api.line.getOneLine($id).then(data => {
      document.querySelector('#start-time').textContent = data.startTime.substring(0, 5);
      document.querySelector('#end-time').textContent = data.endTime.substring(0, 5);
      document.querySelector('#interval').textContent = data.intervalTime;
    });
  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onReadDetailedInfo);
    // $aaa.addEventListener(EVENT_TYPE.CLICK, onReadDetailedInfo);
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
