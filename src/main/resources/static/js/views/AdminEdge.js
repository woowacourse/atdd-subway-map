import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
	const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
	const $subwayLineStationSubmitButton = document.querySelector("#submit-button");
	const $subwayLineInput = document.querySelector("#line-select-options");
	const $subwayDepartInput = document.querySelector("#depart-select-options");
	const $subwayArrivalInput = document.querySelector("#arrival-select-options");
	const createSubwayEdgeModal = new Modal();
	let subwayStations = null;
	let subwayLines = null;

	const getDatasetByOption = value => {
		const selectedIndex = value.selectedIndex;
		return value.options[selectedIndex].dataset;
	}

	const onCreateSubwayEdge = async event => {
		const selectedDepartStationId = getDatasetByOption($subwayDepartInput).stationId;
		const selectedArrivalStationId = getDatasetByOption($subwayArrivalInput).stationId;
		const edge = {
			preStationId: selectedDepartStationId,
			stationId: selectedArrivalStationId,
			distance: 1000,
			duration: 5
		}
		const selectedLineId = getDatasetByOption($subwayLineInput).lineId;
		await api.edge.update(selectedLineId, edge);
	}

	const onDeleteSubwayEdge = async event => {
		const $target = event.target;
		const isDeleteButton = $target.classList.contains("mdi-delete");
		if (isDeleteButton) {
			const selectedLineId = $target.closest("#line-name").dataset.lineId;
			const selectedStationId = $target.closest(".list-item").dataset.stationId;
			$target.closest(".list-item").remove();
			await api.edge.delete(selectedLineId, selectedStationId);
		}
	};

	const initEventListeners = () => {
		$subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayEdge);
		$subwayLineStationSubmitButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayEdge);
	};

	const initSubwayArrivalOptions = () => {
		const subwayStationOptionTemplate = subwayStations
			.map(station => optionTemplate(station))
			.join("");
		$subwayArrivalInput.insertAdjacentHTML(
			"afterbegin",
			subwayStationOptionTemplate
		);
	};

	const initSubwayDepartOptions = () => {
		const nullDepartStationOptionTemplate = `<option data-line-id="null">없음</option>`;
		const subwayStationOptionTemplate = subwayStations
			.map(station => optionTemplate(station))
			.join("");
		$subwayDepartInput.insertAdjacentHTML(
			"afterbegin",
			nullDepartStationOptionTemplate.concat(subwayStationOptionTemplate)
		);
	};

	const initSubwayLineOptions = () => {
		const subwayLineOptionTemplate = subwayLines
			.map(line => optionTemplate(line))
			.join("");
		$subwayLineInput.insertAdjacentHTML(
			"afterbegin",
			subwayLineOptionTemplate
		);
	};

	const initSubwayLinesSlider = async () => {
		subwayLines = await api.line.get();
		initSubwayLineOptions();
		subwayStations = await api.station.get();
		initSubwayDepartOptions();
		initSubwayArrivalOptions();
		$subwayLinesSlider.innerHTML = subwayLines
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

	this.init = () => {
		initSubwayLinesSlider();
		initEventListeners();
	};
}

const adminEdge = new AdminEdge();
adminEdge.init();
