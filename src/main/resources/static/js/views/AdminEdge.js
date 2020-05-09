import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $addSubwayEdgeButton = document.querySelector("#submit-button");
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = async () => {
    const subwayLines = await api.line;
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

  const initSubwayLineOptions = async () => {
    const subwayLines = await api.line.get().then();
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

  const onCreateStationHandler = event => {
    event.preventDefault();
    const $target = event.target;
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
    $addSubwayEdgeButton.addEventListener(EVENT_TYPE.CLICK, onCreateStationHandler);
  };

  this.init = () => {
    initSubwayLinesSlider().then();
    initSubwayLineOptions().then();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
