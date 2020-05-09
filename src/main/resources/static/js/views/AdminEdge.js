import {
  optionTemplate,
  subwayLinesItemTemplate,
} from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();
  const $createSubmitButton = document.querySelector("#submit-button");

  const initSubwayLinesSlider = () => {
    fetch('/lineStations', {
      method:'GET',
    }).then(response=>response.json())
    .then(jsonResponse=>{
      $subwayLinesSlider.innerHTML = jsonResponse
      .map(line=> subwayLinesItemTemplate(line))
      .join("");
      tns({
        container: ".subway-lines-slider",
        loop: true,
        slideBy: "page",
        speed: 400,
        autoplayButtonOutput: false,
        mouseDrag: true,
        lazyLoad: true,
        controlsContainer: "#slider-controls",
        items: 1,
        edgePadding: 25
      });
    });
  };

  const initSubwayLineOptions = () => {

    fetch('/lineStations', {
      method:'GET',
    }).then(response=>response.json())
    .then(jsonResponse=>{
      const subwayLineOptionTemplate = jsonResponse
      .map(line=> optionTemplate(line))
      .join("");
      const $stationSelectOptions = document.querySelector(
        "#station-select-options"
      );
      $stationSelectOptions.insertAdjacentHTML(
        "afterbegin",
        subwayLineOptionTemplate
      );
    });
  };

  const onCreateStationHandler = event => {
    const selectLines = document.querySelector("#station-select-options");

    const data = {
      lineId: selectLines.options[selectLines.selectedIndex].value,
      preStationName: document.querySelector("#depart-station-name").value.trim(),
      stationName: document.querySelector("#arrival-station-name").value.trim()
    }

    fetch('/lineStations', {
      method:'POST',
      headers:{
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }).then(response=>response.json())
    .then(()=>async function(){
      alert("야호");
      const response = await fetch('/lineStations', {
        method:'GET'
      });
      const jsonResponse = await response.json();
      $subwayLinesSlider.innerHTML = jsonResponse
      .map(line=> subwayLinesItemTemplate(line))
      .join("");
      tns({
        container: ".subway-lines-slider",
        loop: true,
        slideBy: "page",
        speed: 400,
        autoplayButtonOutput: false,
        mouseDrag: true,
        lazyLoad: true,
        controlsContainer: "#slider-controls",
        items: 1,
        edgePadding: 25
      });
    }).catch(error=>{
      alert(error);
    });
  }

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();

      const lineId = $target.closest(".list-item").dataset.lineId;
      const stationId = $target.closest(".list-item").dataset.stationId;
      fetch("lineStations/delete/" + lineId+"/"+stationId, {
        method:'DELETE'
      }).catch(alert);
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
    $createSubmitButton.addEventListener(
      EVENT_TYPE.CLICK,
      onCreateStationHandler
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
