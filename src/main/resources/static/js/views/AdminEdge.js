import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $departStation = document.querySelector("#depart-station-name")
  const $arrivalStation = document.querySelector("#arrival-station-name")
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $createEdgeButton = document.querySelector(
    ".mb-4 #submit-button"
  );
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = () => {
    api.line.get().then( data => {
        $subwayLinesSlider.innerHTML = data
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
    });
  };

  const initSubwayLineOptions = () => {
    api.line.get().then(data => {
      const subwayLineOptionTemplate = data
        .map(line => optionTemplate(line))
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

  const onCreateEdge = event => {
    event.preventDefault();
    const selector = document.querySelector("#station-select-options" );
    const selectedOption = selector.options[selector.selectedIndex];
    const lineId = selectedOption.dataset.subwayId;
    var $departStationId = null;
    var $arrivalStationId = null;

    api.station.getByName($departStation.value).then(departData => {
        console.log(departData);
        $departStationId = departData;
        if($departStationId == null){
            alert("이전역 이름이 올바르지 않습니다.");
            return;
        }
        api.station.getByName($arrivalStation.value).then(arrivalData => {
            console.log(arrivalData);
            $arrivalStationId = arrivalData;
            if($arrivalStationId == null){
            alert("다음역 이름이 올바르지 않습니다.");
                return;
            }
            const newSubwayLineData = {
              preStationId: $departStationId,
              stationId: $arrivalStationId
            };
            api.lineStation.create(newSubwayLineData, lineId).then(() => {
              $departStation.value = "";
              $arrivalStation.value = "";
              createSubwayEdgeModal.toggle();
            });
        });
    });
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
    $createEdgeButton.addEventListener(EVENT_TYPE.CLICK, onCreateEdge);
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
