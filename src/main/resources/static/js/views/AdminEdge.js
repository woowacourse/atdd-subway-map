import {
  lineOptionTemplate,
  subwayLinesItemTemplate,
  stationOptionTemplate,
  emptyStationOptionTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import {api} from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $openModalButton = document.querySelector(".modal-open");
  const $lineSelectBox = document.querySelector("#line-select-options");
  const $preStationSelectBox = document.querySelector("#depart-station-name-options");
  const $arrivalStationSelectBox = document.querySelector("#arrival-station-name-options");
  const $addStationSubmitButton = document.querySelector("#submit-button");
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = (linesWithStations) => {
    $subwayLinesSlider.innerHTML = linesWithStations
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

  const initSubwayLineOptions = (linesWithStations) => {
    const subwayLineOptionTemplate = linesWithStations
      .map(line => lineOptionTemplate(line))
      .join("");
    $lineSelectBox.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  const initStationOptions = stations => {
    let stationOptions = stations
      .map(station => stationOptionTemplate(station))
      .join("");
    const $departStationOptions = document.querySelector("#depart-station-name-options");
    const $arrivalStationOptions = document.querySelector("#arrival-station-name-options");
    $arrivalStationOptions.insertAdjacentHTML("afterbegin", stationOptions);
    stationOptions += emptyStationOptionTemplate();
    $departStationOptions.insertAdjacentHTML("afterbegin", stationOptions);
  };

  const onRemoveStationHandler = async event => {
    const $eventTarget = event.target;
    const isDeleteButton = $eventTarget.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $deleteTarget = $eventTarget.closest(".list-item");
      $deleteTarget.remove();
      await api.edge.delete(
        $deleteTarget.dataset.lineId, $deleteTarget.dataset.stationId);
    }
  };

  const onAddStationHandler = async event => {
    event.preventDefault();
    const lineId = $lineSelectBox
      .options[$lineSelectBox.selectedIndex].dataset.lineId;
    const preStationId = $preStationSelectBox
      .options[$preStationSelectBox.selectedIndex].dataset.stationId;
    const stationId = $arrivalStationSelectBox
      .options[$arrivalStationSelectBox.selectedIndex].dataset.stationId;

    const data = {
      preStationId: preStationId,
      stationId: stationId,
      distance: 10,
      duration: 10
    };
    await api.edge.create(lineId, data).catch().finally(() =>
      createSubwayEdgeModal.toggle());
  };

  const initEventListeners =  () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $openModalButton.addEventListener(EVENT_TYPE.CLICK, createSubwayEdgeModal.toggle);
    $addStationSubmitButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  this.init = async () => {
    let linesWithStations = await api.edge.getLinesWithStations();
    let stations = await api.station.get();
    initSubwayLinesSlider(linesWithStations);
    initSubwayLineOptions(linesWithStations);
    initStationOptions(stations);
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
