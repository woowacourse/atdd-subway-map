import {
  currentStationOptionTemplate,
  optionTemplate,
  preStationOptionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLine = document.querySelector("#station-select-options");
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $createSubwayLineStationButton = document.querySelector("#submit-button");
  const $preSubwayStation = document.querySelector("#pre-station-select-options");
  const $currentSubwayStation = document.querySelector("#current-station-select-options");
  const $duration = document.querySelector("#duration");
  const $distance = document.querySelector("#distance");
  const $subwayLinesItemTemplate = document.querySelector("#subway-lines-item-template");
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = async () => {
    let lines = await api.line.get();
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

  const initSubwayLineOptions = async () => {
    let lines = await api.line.get();
    const subwayLineOptionTemplate = lines
    .map(line => optionTemplate(line))
    .join("");
    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );
    $stationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  const initSubwayStationOptions = async () => {
    const stations = await api.station.get();
    const firstNullStation = {
      id: null,
      name: "없음",
    };
    const preSubwayStationOptionTemplate =
      preStationOptionTemplate(firstNullStation) +
      stations.map(station => preStationOptionTemplate(station))
      .join("");
    const currentSubwayStationOptionTemplate = stations
    .map(station => currentStationOptionTemplate(station))
    .join("");
    const $preStationSelectOptions = document.querySelector(
      "#pre-station-select-options"
    );
    $preStationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      preSubwayStationOptionTemplate
    );
    const $currentStationSelectOptions = document.querySelector(
      "#current-station-select-options"
    );
    $currentStationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      currentSubwayStationOptionTemplate
    );
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $button = $target.closest("button");
      const lineId = $button.dataset.lineId;
      const stationId = $button.dataset.stationId;
      api.edge.delete(lineId, stationId);
      $target.closest(".list-item").remove();
    }
  };

  const onCreateLineStationHandler = async event => {
    const lineId = $subwayLine.childNodes[$subwayLine.selectedIndex].dataset.lineId;
    const preStationId = $preSubwayStation.childNodes[$preSubwayStation.selectedIndex].dataset.preStationId;
    const stationId = $currentSubwayStation.childNodes[$currentSubwayStation.selectedIndex].dataset.currentStationId;
    const distance = $distance.value;
    const duration = $duration.value;
    const newSubwayLineStation = {
      preStationId,
      stationId,
      distance,
      duration,
    };
    await api.edge.create(lineId, newSubwayLineStation);
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
    $createSubwayLineStationButton.addEventListener(
      EVENT_TYPE.CLICK,
      onCreateLineStationHandler
    )
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initSubwayStationOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
