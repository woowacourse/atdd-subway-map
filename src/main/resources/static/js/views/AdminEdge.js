import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import {api} from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $openModalButton = document.querySelector(".modal-open");
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
    $openModalButton.addEventListener(
      EVENT_TYPE.CLICK,
      createSubwayEdgeModal.toggle
    );
  };

  this.init = async () => {
    const linesWithStations = await api.edge.getLinesWithStations();
    initSubwayLinesSlider(linesWithStations);
    initSubwayLineOptions(linesWithStations);
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
