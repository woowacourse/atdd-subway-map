import { optionTemplate, subwayLinesItemTemplate, } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE, KEY_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();
  const $createSubmitButton = document.querySelector("#submit-button");
  const $errorMessage = document.querySelector("#error-message");
  const $createSubmit = document.querySelector("#line-station-input");

  const initSubwayLinesSlider = () => {
    let statusCode;

    fetch('/lineStations', {
      method: 'GET',
    }).then(response => {
      if (!response.ok) {
        statusCode = 500;
      }
      return response.json();
    }).then(jsonResponse => {
      if (statusCode !== 500) {
        $subwayLinesSlider.innerHTML = jsonResponse
        .map(line => subwayLinesItemTemplate(line))
        .join("");
        tns({
          container: ".subway-lines-slider",
          loop: true,
          slideBy: "page",
          speed: 400,
          autoplayButtonOutput: false,
          mouseDrag: true,
          lazyLoad: true,
          controlsContainer: "#slider-controls",
          items: 1,
          edgePadding: 25
        });
      } else {
        alert(jsonResponse);
      }
    });
  };

  const initSubwayLineOptions = () => {

    fetch('/lineStations', {
      method: 'GET',
    }).then(response => response.json())
    .then(jsonResponse => {
      const subwayLineOptionTemplate = jsonResponse
      .map(line => optionTemplate(line))
      .join("");
      const $stationSelectOptions = document.querySelector(
        "#station-select-options"
      );
      $stationSelectOptions.insertAdjacentHTML(
        "afterbegin",
        subwayLineOptionTemplate
      );
    });
  };

  const onCreateStationHandler = event => {
    if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
      return;
    }
    let statusCode;
    const selectLines = document.querySelector("#station-select-options");

    const data = {
      lineId: selectLines.options[selectLines.selectedIndex].value,
      preStationName: document.querySelector("#depart-station-name").value.trim(),
      stationName: document.querySelector("#arrival-station-name").value.trim()
    }

    validate(data);

    fetch('/lineStations', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }).then(response => {
      if (response.status >= 400) {
        alert("에러가 발생했습니다.");
        statusCode = 500;
      }
      return response.json();
    }).then(jsonResponse => async function (jsonResponse) {
      if (statusCode !== 500) {
        const response = await fetch('/lineStations', {
          method: 'GET'
        });
        const jsonResponse = await response.json();
        $subwayLinesSlider.innerHTML = jsonResponse
        .map(line => subwayLinesItemTemplate(line))
        .join("");
        tns({
          container: ".subway-lines-slider",
          loop: true,
          slideBy: "page",
          speed: 400,
          autoplayButtonOutput: false,
          mouseDrag: true,
          lazyLoad: true,
          controlsContainer: "#slider-controls",
          items: 1,
          edgePadding: 25
        });
      }
    }).catch(error => {
      throw new Error(error);
    });
  }

  function validate(data) {
    const lineId = data.lineId;
    const list = document.querySelectorAll(".list-item");
    const array = Array.from(list);

    if (duplicatedName(lineId, array, data)) {
      alert("추가하려는 역 이름이 이미 존재합니다.");
      throw new Error();
    }

    if (notExistingPreStationName(lineId, array, data)) {
      alert("이전 역 이름이 적절하지 않습니다.");
      throw new Error();
    }
  }

  function duplicatedName(lineId, array, data) {
    return array.some(element => {
      return element.dataset.lineId === lineId && element.innerText === data.stationName;
    });
  }

  function notExistingPreStationName(lineId, array, data) {
    for (const element of array) {
      if (element.dataset.lineId === lineId && element.innerText === data.preStationName) {
        return false;
      }
    }
    return array.length !== 0;

  }

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();

      const lineId = $target.closest(".list-item").dataset.lineId;
      const stationId = $target.closest(".list-item").dataset.stationId;
      fetch("lineStations/lines/" + lineId + "/stations/" + stationId, {
        method: 'DELETE'
      }).catch(alert);
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
    $createSubmitButton.addEventListener(
      EVENT_TYPE.CLICK,
      onCreateStationHandler
    );
    $createSubmit.addEventListener(
      EVENT_TYPE.KEY_PRESS,
      onCreateStationHandler
    );
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
