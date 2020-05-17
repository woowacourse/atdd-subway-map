import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $saveSubwayLineStationButton = document.querySelector("#submit-button");
  const $stationSelectOptions = document.querySelector("#station-select-options");
  const $departStationName = document.querySelector("#depart-station-name");
  const $arrivalStationName = document.querySelector("#arrival-station-name");

  const createSubwayEdgeModal = new Modal();

  const state = {
    lines: []
  };

  const initSubwayLinesSlider = async () => {
    await api.line.getWithStations().then(lines => {
      $subwayLinesSlider.innerHTML = lines
          .map(line => subwayLinesItemTemplate(line))
          .join("");
    });

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
      edgePadding: 0
    });
  };

  const initSubwayLineOptions = () => {
    api.line.get().then(lines => {
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
    });
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $subwayLinesItem = $target.closest(".subway-lines-item")
      const $listItem = $target.closest(".list-item")
      api.line.deleteLineStation($subwayLinesItem.dataset.lineId, $listItem.dataset.stationId).then(() => {
        $target.closest(".list-item").remove();
      });
    }
  };

  const onSaveLineStationHandler = async event => {
    event.preventDefault();
    const stations = await api.station.get();
    const preStation = stations.find(station => station.name === $departStationName.value);
    const station = stations.find(station => station.name === $arrivalStationName.value);

    const newLineStation = {
      preStationId: preStation && preStation.id || null,
      stationId: station.id,
      distance: 0,
      duration: 0
    }
    await api.line.createLineStation(newLineStation, $stationSelectOptions.options[$stationSelectOptions.selectedIndex].value);
    initSubwayLinesSlider();
    createSubwayEdgeModal.toggle();
  }

  const initEventListeners = () => {
    $saveSubwayLineStationButton.addEventListener(EVENT_TYPE.CLICK, onSaveLineStationHandler);
    $subwayLinesSlider.addEventListener(
        EVENT_TYPE.CLICK,
        onRemoveStationHandler
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
