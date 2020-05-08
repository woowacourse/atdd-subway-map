import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $selectStation = document.querySelector("#station-select-options");
  const $preStation = document.querySelector("#depart-station-name");
  const $currentStation = document.querySelector("#arrival-station-name");

  const createSubwayEdgeModal = new Modal();

  const onCreateSubwayEdge = event => {
    const newSubwayEdge = {
      preStationId: $preStation,
      stationId: $currentStation,
      distance: 10,
      duration: 10
    }

    $selectStation.value = "";
    $preStation.value = "";
    $currentStation.value = "";
  }

  const initSubwayLinesSlider = async () => {
    await api.lines.get().then(data => {
      $subwayLinesSlider.innerHTML = data.map(line => subwayLinesItemTemplate(line))
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
      edgePadding: 25
    });
  };

  const initSubwayLineOptions = async () => {
    let subwayLineOptionTemplate;
    await api.lines.get().then(data => {
      subwayLineOptionTemplate = data.map(line => optionTemplate(line)).join("");
    })

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
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
