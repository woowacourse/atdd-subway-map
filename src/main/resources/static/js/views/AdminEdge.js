import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();

  const $lineSelectOption = document.querySelector('#line-select-options');
  const $preStationSelectOption = document.querySelector('#pre-station-select-options');
  const $stationSelectOption = document.querySelector('#station-select-options');

  const $submitLineStationButton = document.querySelector('#submit-button')

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

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");

    if (isDeleteButton) {
      const $deleteTarget = $target.closest(".list-item");
      const id = $deleteTarget.dataset.station;
      const lineId = $deleteTarget.closest(".target-line").dataset.line;
      $deleteTarget.remove();

      fetch(`/lineStation/${lineId}/rm/${id}`, {
        method: 'DELETE'
      });
    }
  };

  const onSubmitLineStation = async () => {
    const lineId = $lineSelectOption.options[$lineSelectOption.selectedIndex].value;
    const preStationId = $preStationSelectOption.options[$preStationSelectOption.selectedIndex].value;
    const stationId = $stationSelectOption.options[$stationSelectOption.selectedIndex].value;
    const distance = document.querySelector('#line-station-distance').value;
    const duration = document.querySelector('#line-station-duration').value;

    let inputLineStation = {
      line: lineId,
      preStationId: preStationId,
      stationId: stationId,
      distance: distance,
      duration: duration
    }

    return await fetch("/lineStation", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(inputLineStation)
    }).then(() => window.setTimeout(function () {
      location.reload()
    }, 2000))
        .catch(err => console.log(err));
  }

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $submitLineStationButton.addEventListener(EVENT_TYPE.CLICK, onSubmitLineStation)
  };

  this.init = () => {
    initSubwayLinesSlider();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
