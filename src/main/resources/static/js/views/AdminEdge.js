import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $createEdgeButton = document.querySelector(
        "#submit-button"
    );
    const createSubwayEdgeModal = new Modal();

    const initSubwayLinesSlider = () => {
        fetch("/lines", {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'get'
        }).then(res => res.json())
            .then(data => {
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
            })
    };

    const initSubwayLineOptions = () => {
        fetch("/lines", {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'get'
        }).then(res => res.json())
            .then(data => {
                const subwayLineOptionTemplate =
                    data.map(line => optionTemplate(line)).join("");
                const $stationSelectOptions = document.querySelector(
                    "#station-select-options"
                );
                $stationSelectOptions.insertAdjacentHTML(
                    "afterbegin",
                    subwayLineOptionTemplate);
            })
    };

    const createEdge = event => {
        const $selectedIndex = document.querySelector("#station-select-options").selectedIndex;
        const $selectedLineId = document.querySelector("#station-select-options")
            .options[$selectedIndex].dataset.lineId;
        const $preStationName = document.querySelector("#depart-station-name").value;
        const $stationName = document.querySelector("#arrival-station-name").value;
        const $distance = document.querySelector("#distance").value;
        const $duration = document.querySelector("#duration").value;

        fetch("lines/" + $selectedLineId + "/stations", {
            method: 'post',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                preStationName: $preStationName,
                stationName: $stationName,
                distance: $distance,
                duration: $duration,
            })
        }).then(res => {
            if (!res.ok){
                throw res;
            }
        }).catch(err => {
            err.text().then(msg => alert(msg));
        })
    }

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const $selectedLineId = document.querySelector("#optionTitle").dataset.lineId
        const $selectedStationId = $target.closest(".list-item").dataset.stationId
        const isDeleteButton = $target.classList.contains("mdi-delete");

        if (isDeleteButton && confirm("삭제하시겠습니까?")) {
            const url = "lines/" + $selectedLineId + "/stations/" + $selectedStationId;
            fetch(url, {
                method: 'delete'
            }).then(res => {
                if (res.ok){
                    $target.closest(".list-item").remove()
                }
            })
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $createEdgeButton.addEventListener(EVENT_TYPE.CLICK, createEdge)
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
