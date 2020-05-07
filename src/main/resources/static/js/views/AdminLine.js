import { EVENT_TYPE } from "../../utils/constants.js";
import {
  subwayLinesTemplate,
  colorSelectOptionTemplate
} from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import Modal from "../../ui/Modal.js";
import { ERROR_MESSAGE } from '../../utils/constants.js';

function AdminLine() {
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

  const onCreateSubwayLine = event => {
    event.preventDefault();


    let data = {
      name: $subwayLineNameInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value,
      bgColor: $subwayLineColorInput.value
    };

    validate(data);

    fetch("/lines", {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }).then(response => response.json())
    .then(jsonResponse=> {
      const newSubwayLine = {
        id: jsonResponse.id,
        name: jsonResponse.name,
        bgColor: jsonResponse.bgColor
      };
      $subwayLineList.insertAdjacentHTML(
        "beforeend",
        subwayLinesTemplate(newSubwayLine)
      );
    });

    subwayLineModal.toggle();
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
  };

  function validate(line) {
    if (!line.name || !line.startTime || !line.endTime || !line.intervalTime || !line.bgColor) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      throw new Error();
    }
    if (line.name.includes(" ")) {
      alert(ERROR_MESSAGE.NOT_BLANK);
      throw new Error();
    }
    //myCode
    if (duplicatedName(line.name)) {
      alert(ERROR_MESSAGE.DUPLICATE_NAME);
      throw new Error();
    }

  }

  function duplicatedName(input) {
    const names = document.querySelectorAll(".subway-line-item");
    const namesArr = Array.from(names);
    return namesArr.some(element => {
      return element.innerText.trim() === input;
    });
  }

  const onDeleteSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".subway-line-item").remove();
    }
  };

  const onReadSubwayLine = event => {
    const $target = event.target;
    const isSubwayLineItem = $target.classList.contains("subway-line-item");
    if (isSubwayLineItem) {
      const subwayLine ={
        id: document.querySelector(".line-id").innerText.trim()
      };

      fetch("/lines/"+subwayLine.id, {
        method:'GET',
        headers:{
          'Content-Type' : 'application/json; charset=utf-8'
        }
      }).then(response=>response.json())
      .then(jsonResponse=>{
        document.querySelector("#first-time-display").innerText = jsonResponse.startTime.toString().substr(0, 5);
        document.querySelector("#last-time-display").innerText = jsonResponse.endTime.toString().substr(0, 5);
        document.querySelector("#interval-time-display").innerText = jsonResponse.intervalTime + "분";
      });
    }
  }

  const onUpdateSubwayLine = event => {
    event.preventDefault();
    const $target = event.target;
    const isUpdateButton = $target.classList.contains("mdi-pencil");
    if (isUpdateButton) {
      subwayLineModal.toggle();

      const updatedSubwayLine = {
        name: $subwayLineNameInput.value,
        bgColor: $subwayLineColorInput.value
      };

      const data = {
        name: $subwayLineNameInput.value,
        startTime: $subwayLineFirstTimeInput.value,
        endTime: $subwayLineLastTimeInput.value,
        intervalTime: $subwayLineIntervalTimeInput.value,
        bgColor: $subwayLineColorInput.value
      }

      fetch("")

    }
  };

  const onEditSubwayLine = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-pencil");
  };

  const initDefaultSubwayLines = () => {
    fetch("/lines", {
      method: "GET",
      headers: {
        'Content-type': 'application/json'
      }
    }).then(response => response.json())
    .then(jsonResponse=>{
      for (const line of jsonResponse) {
        $subwayLineList.insertAdjacentHTML("beforeend", subwayLinesTemplate(line));
      }
    }).catch(error=>{
      alert(error);
    });

  };

  const initEventListeners = () => {
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
    $subwayLineList.addEventListener(EVENT_TYPE.CLICK, onReadSubwayLine);
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
