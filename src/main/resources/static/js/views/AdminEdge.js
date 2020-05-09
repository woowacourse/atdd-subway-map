import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { CLICK_TYPE, EVENT_TYPE, KEY_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
	const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
	const $subwayLineStationSubmitButton = document.querySelector("#submit-button");
	const $subwayLineSelectionInput = document.querySelector("#station-select-options");
	const $subwayDepartStationInput = document.querySelector("#depart-station-name");
	const $subwayArrivalStationInput = document.querySelector("#arrival-station-name");
	const createSubwayEdgeModal = new Modal();

	const clearSubwayEdgeForm = event => {
		$subwayDepartStationInput.value = "";
		$subwayArrivalStationInput.value = "";
	};

	const initSubwayLinesSlider = async () => {
		const lines = await api.line.get();
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

	const initSubwayLineOptions = async () => {
		const lines = await api.line.get();
		const subwayLineOptionTemplate = lines
			.map(line => optionTemplate(line))
			.join("");
		$subwayLineSelectionInput.insertAdjacentHTML(
			"afterbegin",
			subwayLineOptionTemplate
		);
	};

	const onCreateSubwayEdge = async event => {
		if (isInvalidKey(event)) {
			return;
		}
		const edge = {
			preStationName: $subwayDepartStationInput.value,
			stationName: $subwayArrivalStationInput.value,
			distance: 1000,
			duration: 5
		}
		const selectedLineIndex = $subwayLineSelectionInput.selectedIndex;
		const selectedLineId = $subwayLineSelectionInput.options[selectedLineIndex].dataset.lineId;
		if (event.key === KEY_TYPE.ENTER) {
			window.location.reload();
		}
		await api.edge.update(selectedLineId, edge);
	}

	const isInvalidKey = event => {
		return (event.key !== KEY_TYPE.ENTER) && (event.button !== CLICK_TYPE.LEFT_CLICK);
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
		$subwayArrivalStationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onCreateSubwayEdge);
		createSubwayEdgeModal.$closeModalButton.addEventListener(EVENT_TYPE.CLICK, clearSubwayEdgeForm);
	};

	this.init = () => {
		initSubwayLinesSlider().then();
		initSubwayLineOptions().then();
		initEventListeners();
	};
}

const adminEdge = new AdminEdge();
adminEdge.init();
