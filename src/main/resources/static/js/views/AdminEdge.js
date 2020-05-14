import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  let subwayLines = [];
  let stations = [];

  const $openModalButton = document.querySelector(".modal-open");
  let $subwayLinesSlider = document.querySelector(".subway-lines-slider");
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
          subwayLines.map(line => line.id === lineId ? line.filter(station => station !== stationId) : line);
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
      });
  };

  const onAddStationHandler = (event) => {
    event.preventDefault();
    const lineName = $lineSelect.value;
    const lineId = subwayLines.find(line => line.name === lineName).id;
    const departStationName = $departStationInput.value.trim();
    const arrivalStationName = $arrivalStationInput.value.trim();
    const stationsIdInLine = subwayLines.find(line => line.id === lineId).stations
      .map(station => station.id);
    console.log(stationsIdInLine);

    if (!stations.some(station => station.name === departStationName)) {
      alert("입력한 출발역은 존재하지 않습니다.");
      return;
    }
    if (!stations.some(station => station.name === arrivalStationName)) {
      alert("입력한 도착역은 존재하지 않습니다.");
      return;
    }

    const departStationId = stations.find(station => station.name === departStationName).id;
    const arrivalStationId = stations.find(station => station.name === arrivalStationName).id;

    const resetSlider = () => {
      document.querySelector(".w-full.h-full").innerHTML = `<div class="subway-lines-slider"></div>`;
      $subwayLinesSlider = document.querySelector(".subway-lines-slider");
      initSubwayLinesSlider();
      $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    };

    api.line.addStation(lineId, { preStationId: departStationId, stationId: arrivalStationId })
      .then(response => {
        subwayLines = subwayLines.map(line => line.id === response.id ? response : line);
        resetSlider();
      })
      .catch(error => console.log(error))
      .finally(() => {
        createSubwayEdgeModal.toggle();
      });
  };

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
