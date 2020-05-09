import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";


function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $createStationToLineButton = document.querySelector("#submit-button");
  const $lineIdInput = document.querySelector("#station-select-options");
  const $preStationInput = document.querySelector("#depart-station-name");
  const $stationInput = document.querySelector("#arrival-station-name");

  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = async () => {
    await api.line.get()
      .then(data => {
        $subwayLinesSlider.innerHTML = data
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
      edgePadding: 25
    });
  };

  const initSubwayLineOptions = async () => {
    const subwayLineOptionTemplate = await api.line.get()
      .then(data => {
        return data
          .map(line => optionTemplate(line))
          .join("");
      });

    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );

    $stationSelectOptions.insertAdjacentHTML("afterbegin", subwayLineOptionTemplate);
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const onCreateStationToLine = event => {
    event.preventDefault();
    const lindId = $lineIdInput.options[$lineIdInput.selectedIndex].dataset.id;
    const preStationName = $preStationInput.value;
    const stationName = $stationInput.value;

    const request = {
      lineId: lindId,
      preStationName: preStationName,
      stationName: stationName
    };
    api.lineStation.create(request)
      .then(data => console.log(data));
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
    $createStationToLineButton.addEventListener(EVENT_TYPE.CLICK, onCreateStationToLine);
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
