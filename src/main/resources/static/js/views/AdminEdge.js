import {listItemTemplate, optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
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

  let stations = [];

  const initSubwayLinesSlider = async () => {
    stations = await api.station.get();

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
      const lineId = $target.closest(".line").getAttribute("data-line-id");
      const targetId = $target.closest("div").dataset.stationId;

      api.lineStation.delete(lineId, targetId);

      $target.closest(".list-item").remove();
    }
  };

  const onCreateStationToLine = async event => {
    event.preventDefault();

    const lineId = $lineIdInput.options[$lineIdInput.selectedIndex].dataset.id;
    const preStationName = $preStationInput.value;
    const stationName = $stationInput.value;
    const preStation = await stations.find(station => station.name === preStationName);
    const station = await stations.find(station => station.name === stationName);
    const preStationId = preStationName === "" ? null : preStation ? preStation["id"] : undefined;
    const stationId = stationName ? station["id"] : undefined;

    if (!lineId || preStationId === undefined || !stationId) {
      alert('유효한 값을 입력해주세요');
      return;
    }

    const request = {
      lineId: lineId,
      preStationId: preStationId,
      stationId: stationId
    };

    await api.lineStation.create(request)
      .then(data => {
        const listItem = listItemTemplate(data);
        $subwayLinesSlider.insertAdjacentHTML("beforeend", listItem);
      });
    createSubwayEdgeModal.toggle();
    location.href = location.href;
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
