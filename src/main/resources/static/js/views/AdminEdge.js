import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $stationSelectOptions = document.querySelector('#station-select-options');
  const $departStationName = document.querySelector('#depart-station-name');
  const $arrivalStationName = document.querySelector('#arrival-station-name');
  const $submitButton = document.querySelector('#submit-button');
  const createSubwayEdgeModal = new Modal();

  const convertLines = (lines) => {
    const newLine = [];
    for (let l of lines) {
      let o = {};
      o.title = l.name;
      o.bgColor = l.color;
      o.stations = [];
      for (let s of l.stations) {
        o.stations.push(s.name);
      }
      newLine.push(o);
    }
    return newLine;
  }

  const getLines = () => {
    return fetch("/lineStations")
    .then(res => res.json());
  }

  const initSubwayLinesSlider = async () => {
    const persistLines = await getLines();
    const lines = convertLines(persistLines);
    $subwayLinesSlider.innerHTML = lines
    .map(line => subwayLinesItemTemplate(line))
    .join("");
    tns({
      container: ".subway-lines-slider",
      loop: true,
      slideBy: "page",
      speed: 400,
      autoplayButtonOutput: false,
      mouseDrag: true,
      lazyload: true,
      controlsContainer: "#slider-controls",
      items: 1,
      edgePadding: 25
    });
  };

  const initSubwayLineOptions = () => {
    const subwayLineOptionTemplate = defaultSubwayLines
    .map(line => optionTemplate(line.title))
    .join("");
    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );
    $stationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  const onAddStationHandler = event => {
    event.preventDefault();
    const stationName = $arrivalStationName.value;
    const data = {
      lineName: $stationSelectOptions.value,
      preStationName: $departStationName.value,
      stationName: stationName
    };
    console.log("data", data);

    fetch("/lineStation", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    // TODO: 새로고침 없이 반영되도록 변경
    location.reload();
    createSubwayEdgeModal.toggle();
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $submitButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
