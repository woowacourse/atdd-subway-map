import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import tns from "../../lib/slider/tiny-slider.js";
import api from '../../api/index.js';

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $lineSelectOptions = document.querySelector('#line-select-options');
  const $departSelectOptions = document.querySelector('#depart-select-options');
  const $arrivalSelectOptions = document.querySelector('#arrival-select-options');
  const $submitButton = document.querySelector('#submit-button');
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = async () => {
    const lines = await api.edge.get();

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

  const initStationOptions = async () => {
    const stations = await api.station.get();
    const stationOptionTemplate = stations
    .map(station => optionTemplate(station.id, station.name))
    .join("");

    $departSelectOptions.insertAdjacentHTML("afterbegin", optionTemplate(null, ""));
    $departSelectOptions.insertAdjacentHTML("beforeend", stationOptionTemplate);
    $arrivalSelectOptions.insertAdjacentHTML("beforeend", stationOptionTemplate);
  }

  const initSubwayLineOptions = async () => {
    const subwayLines = await api.line.get();
    const subwayLineOptionTemplate = subwayLines
    .map(line => optionTemplate(line.id, line.name))
    .join("");

    $lineSelectOptions.insertAdjacentHTML("afterbegin", subwayLineOptionTemplate);
  };

  const onAddStationHandler = event => {
    event.preventDefault();
    const data = {
      lineId: $lineSelectOptions.selectedOptions[0].dataset.id,
      preStationId: $departSelectOptions.selectedOptions[0].dataset.id,
      stationId: $arrivalSelectOptions.selectedOptions[0].dataset.id
    };

    api.edge.create(data).then(() => {
      location.reload();
      createSubwayEdgeModal.toggle();
    });
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $listItem = $target.closest(".list-item");
      const lineId = $target.closest(".line-station").dataset.lineId;
      const stationId = $listItem.dataset.stationId;

      $listItem.remove();
      api.edge.delete(lineId, stationId);
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $submitButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initStationOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
