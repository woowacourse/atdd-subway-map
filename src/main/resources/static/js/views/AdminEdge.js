import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import {api} from "../../api/index.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $openModalButton = document.querySelector(".modal-open");
  const $lineStationPreStationId = document.querySelector("#depart-station-name");
  const $lineStationStationId = document.querySelector("#arrival-station-name");
  const $lineStationLine = document.querySelector("#station-select-options");
  const createSubwayEdgeModal = new Modal();

  const getLinesWithStations = async function () {
    return await api.lineStation.getLines();
  };

  const onToggleModalForCreate = event => {
    createSubwayEdgeModal.toggle();
  }

  const onCreatelineStation = async event => {
    let data = {
      preStationId: $lineStationPreStationId.value,
      stationId: $lineStationLine.value,
      distance: 10,
      duration: 10
    }
    const response = await api.lineStation.create($lineStationLine.value, data);
    console.log(response);
    $subwayLineList.insertAdjacentHTML(
        "beforeend",
    );
    subwayLineModal.toggle();
  };

  const initSubwayLinesSlider = () => {
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
    const lines = await getLinesWithStations()
    console.log(lines)
    console.log(defaultSubwayLines)
    $subwayLinesSlider.innerHTML = lines
        .map(line => subwayLinesItemTemplate(line))
        .join("");
    const subwayLineOptionTemplate = lines
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

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
    $openModalButton.addEventListener(EVENT_TYPE.CLICK, onToggleModalForCreate);
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
