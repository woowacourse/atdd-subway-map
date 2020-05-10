import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $stationSelectOptions = document.querySelector('#station-select-options');
  const $departStationName = document.querySelector('#depart-station-name');
  const $arrivalStationName = document.querySelector('#arrival-station-name');
  const $submitButton = document.querySelector('#submit-button');
  const createSubwayEdgeModal = new Modal();

  const getLines = () => {
    return fetch("/lineStations")
    .then(res => res.json());
  }

  // TODO: CUD 모두 id로 변경
  const initSubwayLinesSlider = async () => {
    const lines = await getLines();

    $subwayLinesSlider.innerHTML = lines
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

  const onAddStationHandler = event => {
    event.preventDefault();
    const stationName = $arrivalStationName.value;
    const data = {
      lineName: $stationSelectOptions.value,
      preStationName: $departStationName.value,
      stationName: stationName
    };

    fetch("/lineStations", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    });

    // TODO: 새로고침 없이 반영되도록 변경
    location.reload();
    createSubwayEdgeModal.toggle();
  };

  const onRemoveStationHandler = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $listItem = $target.closest(".list-item");
      const lineId = $target.closest(".line-station").dataset.lineId;
      const stationId = $listItem.dataset.stationId;
      $listItem.remove();
      await fetch(`lineStations/${lineId}/${stationId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json"
        },
      }).then(res => res.json());
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $submitButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
