import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = () => {
    api.line.get()
      .then(data => {
        data.map(line => subwayLinesItemTemplate(line)).join("");
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
      edgePadding: 25
    });
  };

  const initSubwayLineOptions = () => {
    api.line.get()
      .then(data => {
        const subwayLineOptionTemplate = data.map(line => optionTemplate(line.name));
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
      const lineId = $target.closest(".tns-item").dataset.lineId;
      const stationId = $target.closest(".list-item").dataset.stationId;
      api.lineStation.delete(lineId, stationId)
        .then(() => $target.closest(".list-item").remove());
    }
  };

  const initEventListeners = () => {
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
