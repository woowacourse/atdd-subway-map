import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $lineInput = document.querySelector("#station-select-options");
  const $departStationInput = document.querySelector("#depart-station-name");
  const $arrivalStationInput = document.querySelector("#arrival-station-name");
  const createSubwayEdgeModal = new Modal();

  let subwayLines = [];
  let subwayStations = [];

  const $createLineStationButton = document.querySelector(
    ".mb-4 #submit-button"
  );

  const initSubwayLinesSlider = async () => {
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
    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );
    $stationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  const onCreateSubwayLine = async event => {
    event.preventDefault();
    const lineId = subwayLines.find(line => line.name === $lineInput.value).id;
    const newLineStation = {
      preStationId: subwayStations.find(station => station.name === $departStationInput.value).id,
      stationId: subwayStations.find(station => station.name === $arrivalStationInput.value).id,
    };
    const newLine = await api.lineStation.create(lineId, newLineStation);
    subwayLines = subwayLines.filter(subwayLine => subwayLine.id !== newLine.id);
    subwayLines.push(newLine);
    await initSubwayLinesSlider();
    createSubwayEdgeModal.toggle();
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const stationName = $target.closest('.list-item').textContent.trim();
    const lineName = $target.closest('.line-name').dataset.lineName;
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
      const deleteLine = subwayLines.find(line => line.name === lineName);
      const deleteStation = subwayStations.find(station => station.name === stationName);
      api.lineStation.delete(deleteLine.id, deleteStation.id);
    }
  };

  const initEventListeners = () => {
    $createLineStationButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
  };

  const initSubwayInfo = async () => {
    subwayLines = [...await api.lines.get()];
    subwayStations = [...await api.station.get()];
    initSubwayLinesSlider();
    initSubwayLineOptions();
  };

  this.init = () => {
    initSubwayInfo();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
