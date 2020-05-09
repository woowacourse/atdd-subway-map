import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  let subwayLines = [];
  let stations = [];

  const $openModalButton = document.querySelector(".modal-open");
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();
  const $addStationButton = document.querySelector("#submit-button");
  const $departStationInput = document.querySelector("#depart-station-name");
  const $arrivalStationInput = document.querySelector("#arrival-station-name");
  const $lineSelect = document.querySelector("#station-select-options");

  const initSubwayLinesSlider = () => {
    $subwayLinesSlider.innerHTML = subwayLines
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
    const subwayLineOptionTemplate = subwayLines
    .map(line => optionTemplate(line.name))
    .join("");
    const $stationSelectOptions = document.querySelector("#station-select-options");
    $stationSelectOptions.insertAdjacentHTML("afterbegin", subwayLineOptionTemplate);
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const lineId = parseInt($target.closest(".slider-list").dataset.lineId);
      const stationId = parseInt($target.closest(".list-item").dataset.stationId);

      api.line.deleteStation(lineId, stationId)
      .then(() => {
        subwayLines.map(line => {
          if (line => line.id === lineId) {
            line = line.filter(station => station !== stationId);
          }
          return line;
        })
      })
      .catch(error => {
        console.log(error);
      });
      $target.closest(".list-item").remove();
    }

  };

  const initDefaultLines = () => {
    api.line.getAll()
    .then(data => {
      subwayLines = data;
      initSubwayLinesSlider();
      initSubwayLineOptions();
    });
  };

  const initDefaultStations = () => {
    api.station.getAll()
    .then(data => {
      stations = data;
    })
  };

  function onAddStationHandler(event) {
    event.preventDefault();
    const lineName = $lineSelect.value;
    const preStationName = $departStationInput.value.trim();
    const stationName = $arrivalStationInput.value.trim();

    const lineId = subwayLines.find(line => line.name === lineName).id;
    const preStationId = stations.find(station => station.name === preStationName).id;
    const stationId = stations.find(station => station.name === stationName).id;

    api.line.addStation(lineId, { preStationId, stationId })
    .then(response => {
      subwayLines = subwayLines.map(line => {
        if (line => line.id === response.id) {
          line = response;
          // 추가될 내용 들어갈 자리 찾기
          $subwayLinesSlider.insertAdjacentHTML("afterbegin", subwayLinesItemTemplate(line));
        }
        return line;
      });
    })
    .catch(error => console.log(error));
    createSubwayEdgeModal.toggle();
  }

  function onClickModalOpen() {
    $lineSelect.value = "";
    $departStationInput.value = "";
    $arrivalStationInput.value = "";
  }

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $addStationButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    $openModalButton.addEventListener(EVENT_TYPE.CLICK, onClickModalOpen);
  };

  this.init = () => {
    initDefaultLines();
    initDefaultStations();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
