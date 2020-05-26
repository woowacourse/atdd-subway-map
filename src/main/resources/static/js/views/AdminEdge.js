import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $subwayLinesSubmitButton = document.querySelector("#submit-button");
    const createSubwayEdgeModal = new Modal();
    const initSubwayLinesSlider = res => {
        $subwayLinesSlider.innerHTML = res
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
    const initSubwayLineOptions = res => {
        const subwayLineOptionTemplate = res
            .map(line => optionTemplate(line))
            .join("");
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
        if (!isDeleteButton) {
            return;
        }
        const lineId = $target.closest(".slider-list").querySelector(
            ".lint-title").dataset.lineId;
        const stationId = $target.closest(".list-item").dataset.stationId;
        api.line.deleteEdge(lineId, stationId)
            .then(() =>
                $target.closest(".list-item").remove()
            ).catch(err => alert(err));
    };
    const onAddEdgeHandler = event => {
        event.preventDefault();
        const $lineSelectBox = document.querySelector(
            "#station-select-options");
        const selectedIndex = $lineSelectBox.selectedIndex;
        const lineId = $lineSelectBox[selectedIndex].dataset.lineId;
        const preStationName = document.querySelector(
            '#depart-station-name').value;
        const stationName = document.querySelector(
            '#arrival-station-name').value;
        const edgeInfo = {
            preStationName: preStationName,
            stationName: stationName,
            distance: 0,
            duration: 0,
        };
        console.log(edgeInfo);
        api.line.createEdge(lineId, edgeInfo).then(res => {
            if (res.status !== 201) {
                alert("추가 중 오류가 발생했습니다.");
                return;
            }
            window.location.reload();
        });
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $subwayLinesSubmitButton.addEventListener(
            EVENT_TYPE.CLICK,
            onAddEdgeHandler
        )
    };
    this.init = async () => {
        const lines = await api.line.getLinesWithStations().then(res => res);
        initSubwayLinesSlider(lines);
        initSubwayLineOptions(lines);
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();