import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import {api} from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $openModalButton = document.querySelector(".modal-open");
  const $lineStationPreStation = document.querySelector("#depart-station-name");
  const $lineStationStation = document.querySelector("#arrival-station-name");
  const $lineStationLine = document.querySelector("#line-select-options");
  const $lineStationCreateButton = document.querySelector('#submit-button')
  const createSubwayEdgeModal = new Modal();
  let subwayLines = []

  const onToggleModalForCreate = event => {
    createSubwayEdgeModal.toggle();
  }

  const onCreateLineStation = event => {
    event.preventDefault()
    let data = {
      preStationId: $lineStationPreStation.value,
      stationId: $lineStationStation.value,
      distance: 10,
      duration: 10
    }
    api.lineStation
        .create($lineStationLine.value, data)
        .then(() => {
          createSubwayEdgeModal.toggle()
          initSubwayLinesView()
        })
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

  const initSubwayLinesView = () => {
    api.lineStation
        .getLines()
        .then(data => {
          subwayLines = data
          if (subwayLines.length > 0) {
            $subwayLinesSlider.innerHTML = subwayLines.map(line => subwayLinesItemTemplate(line)).join('')
            initSubwayLinesSlider()
          }
        })
    const subwayLineOptionTemplate = subwayLines.map(line => optionTemplate(line)).join('')
    const $lineSelectOption = document.querySelector('#line-select-options')
    $lineSelectOption.innerHTML = subwayLineOptionTemplate
    initStationOptions()
  }

  const initStationOptions = () => {
    api.station
        .get()
        .then(stations => {
          $lineStationPreStation.innerHTML = stations.map(station => optionTemplate(station)).join('')
          $lineStationStation.innerHTML = stations.map(station => optionTemplate(station)).join('')
        })
  }

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $openModalButton.addEventListener(EVENT_TYPE.CLICK, onToggleModalForCreate);
    $lineStationCreateButton.addEventListener(EVENT_TYPE.CLICK, onCreateLineStation);
  };

  this.init = () => {
    initSubwayLinesView();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
