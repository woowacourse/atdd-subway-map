import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  let subwayLines = [];
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = () => {
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
    const $stationSelectOptions = document.querySelector("#station-select-options");
    $stationSelectOptions.insertAdjacentHTML("afterbegin", subwayLineOptionTemplate);
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const lineId = parseInt($target.closest(".slider-list").dataset.lineId);
      const stationId = parseInt($target.closest(".list-item").dataset.stationId);
      api.line.deleteStation(lineId, stationId)
      .then(() => {
        subwayLines = subwayLines.filter(line => line.station.id !== stationId);
      })
      .catch(error => {
        console.log(error);
      });
      $target.closest(".list-item").remove();
    }
  };

  const initDefaultLines = () => {
    api.line.getAll()
      .then(data => {
        subwayLines = data;
        initSubwayLinesSlider();
        initSubwayLineOptions();
      });
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
  };

  this.init = () => {
    initDefaultLines();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
