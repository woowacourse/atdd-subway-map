import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js"

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $createEdgeButton = document.querySelector("#submit-button");
    const $selectedLine = document.querySelector("#station-select-options");
    const $preStationNameInput = document.querySelector("#depart-station-name");
    const $stationNameInput = document.querySelector("#arrival-station-name");

    const createSubwayEdgeModal = new Modal();

    async function initSubwayLinesSlider() {
        let subwayLineInfos = await api.edge.get()
            .then(data => data);
        $subwayLinesSlider.innerHTML = subwayLineInfos
            .map(subwayLineInfo => subwayLinesItemTemplate(subwayLineInfo)) // line id 뿌려주기
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

    async function initSubwayLineOptions() {
        let subwayLineInfos = await api.edge.get()
            .then(data => data);

        const subwayLineOptionTemplate = subwayLineInfos
            .map(subwayLineInfo => optionTemplate(subwayLineInfo))
            .join("");
        const $stationSelectOptions = document.querySelector(
            "#station-select-options"
        );
        $stationSelectOptions.insertAdjacentHTML(
            "afterbegin",
            subwayLineOptionTemplate
        );
    };

    const onCreateEdgeHandler = event => {
        const $target = event.target;

        const selectedIndex = $selectedLine.selectedIndex;
        const lineId = $selectedLine.options[selectedIndex].getAttribute("data-line-id");

        const newEdge = {
            preStationName: $preStationNameInput.value,
            stationName: $stationNameInput.value,
            distance: 0,
            duration: 0
        };

        api.edge.post(newEdge, lineId);
        createSubwayEdgeModal.toggle();
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
        $createEdgeButton.addEventListener(EVENT_TYPE.CLICK, onCreateEdgeHandler);
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
