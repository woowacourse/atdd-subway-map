import { EVENT_TYPE, ERROR_MESSAGE, KEY_TYPE } from '../../utils/constants.js'
import { listItemTemplate } from '../../utils/templates.js'
import api from '../../api/index.js'

function AdminStation() {
  const $stationInput = document.querySelector('#station-name')
  const $stationList = document.querySelector('#station-list')
  const $stationAddButton = document.querySelector('#station-add-btn')

  const onAddStationHandler = event => {
    if (event.key && event.key !== KEY_TYPE.ENTER) {
      return
    }
    event.preventDefault()
    const $stationNameInput = document.querySelector('#station-name')
    const stationName = $stationNameInput.value
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY)
      return
    }
    const newStation = {
      name: stationName
    }
    api.station
        .create(newStation)
        .then(data => {
          $stationNameInput.value = ''
          $stationList.insertAdjacentHTML('beforeend', listItemTemplate(data))
        })
        .catch(() => {
          alert('에러가 발생했습니다.')
        })
  }

  const onRemoveStationHandler = event => {
    const $target = event.target
    const isDeleteButton = $target.classList.contains('mdi-delete')
    if (!isDeleteButton) {
      return
    }
    api.station
        .delete($target.closest('.list-item').dataset.id)
        .then(() => {
          $target.closest('.list-item').remove()
        })
        .catch(() => alert(ERROR_MESSAGE.COMMON))
  }

  const initStations = () => {
    api.station
        .getAll()
        .then(stations => {
          $stationList.innerHTML = stations.map(station => listItemTemplate(station)).join('')
        })
        .catch(() => alert(ERROR_MESSAGE.COMMON))
  }

  const initEventListeners = () => {
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler)
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler)
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler)
  }

  const init = () => {
    initEventListeners()
    initStations()
  }

  return {
    init
  }
}

const adminStation = new AdminStation()
adminStation.init()
