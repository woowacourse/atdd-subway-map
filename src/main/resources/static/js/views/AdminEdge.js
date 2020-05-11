import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import Api from "../../api";
import {subwayLineInfoTemplate, subwayLinesTemplate} from "../../utils/templates";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $subwayLineAddButton = document.querySelector("#subway-line-add-btn");
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = () => {
    $subwayLinesSlider.innerHTML = defaultSubwayLines
        .map(line => subwayLinesItemTemplate(line))
        .join("");
    Console.log(defaultSubwayLines.stations);
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
    const subwayLineOptionTemplate = defaultSubwayLines
        .map(line => optionTemplate(line.title))
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

  const onAddStationHandler = event => {
    event.preventDefault();
    defaultSubwayLines.map()

  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
        EVENT_TYPE.CLICK,
        onRemoveStationHandler
    );
    $subwayLineAddButton.addEventListener(
        EVENT_TYPE.CLICK,
        onAddStationHandler
    )
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
