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
      if(data.status == 400){
        throw new Error(data.message);
      }
      if(data.status == 500){
        throw new Error("Unexpected Internal Error");
      }
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
    }).catch(error => alert(error));
  };

  const initSubwayLineOptions = () => {
    api.line.get().then(data => {
      if(data.status == 400){
        throw new Error(data.message);
      }
      if(data.status == 500){
        throw new Error("Unexpected Internal Error");
      }
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
    }).catch(error => alert(error));
  };

  const onCreateEdge = event => {
    event.preventDefault();
    const selector = document.querySelector("#station-select-options" );
    const selectedOption = selector.options[selector.selectedIndex];
    const lineId = selectedOption.dataset.subwayId;
    var $departStationId = null;
    var $arrivalStationId = null;

    api.station.getByName($departStation.value).then(departData => {
      if(departData.status == 400){
        throw new Error(data.message);
      }
      if(departData.status == 500){
        throw new Error("Unexpected Internal Error");
      }
      $departStationId = departData;
      if($departStationId == null){
          alert("이전역 이름이 올바르지 않습니다.");
          return;
      }
      api.station.getByName($arrivalStation.value).then(arrivalData => {
        if(arrivalData.status == 400){
          throw new Error(data.message);
        }
        if(arrivalData.status == 500){
          throw new Error("Unexpected Internal Error");
        }
        $arrivalStationId = arrivalData;
        console.log(arrivalData);
        if($arrivalStationId == null || $arrivalStationId == 0){
        alert("다음역 이름이 올바르지 않습니다.");
            return;
        }
        const newSubwayLineData = {
          preStationId: $departStationId,
          stationId: $arrivalStationId
        };
        api.lineStation.create(newSubwayLineData, lineId).then(data => {
          if(arrivalData.status == 400){
            throw new Error(data.message);
          }
          if(arrivalData.status == 500){
            throw new Error("Unexpected Internal Error");
          }
          initSubwayLinesSlider();
          initSubwayLineOptions();
          createSubwayEdgeModal.toggle();
          $departStation.value = "";
          $arrivalStation.value = "";
        }).catch(error => alert(error));
      }).catch(error => alert(error));
    }).catch(error => alert(error));
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const $targetParent = $target.closest(".list-item");
    const $name = $targetParent.innerText;
    const $lineId = $target.closest(".rounded-sm").querySelector(".station").dataset.subwayId;
    api.station.getByName($name).then(getData => {
      if(getData.status == 400){
        throw new Error(data.message);
      }
      if(getData.status == 500){
        throw new Error("Unexpected Internal Error");
      }
      if (isDeleteButton) {
        if(confirm("정말로 삭제하시겠습니까?")){
          $targetParent.remove();
          api.lineStation.delete($lineId ,stationId).then(deleteData => {
            if(deleteData.status == 400){
              throw new Error(data.message);
            }
            if(deleteData.status == 500){
              throw new Error("Unexpected Internal Error");
            }
          }).catch(error => alert(error));
        }
      }
    }).catch(error => alert(error));
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
