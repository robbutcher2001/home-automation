import { call, put, takeLatest } from 'redux-saga/effects';

import {
  DETERMINE_USER_LOCATION,
  DETERMINE_USER_LOCATION_SUCCESS,
  DETERMINE_USER_LOCATION_FAILURE
} from '../../globals';

const getDataSuccessAction = payload => ({ type: DETERMINE_USER_LOCATION_SUCCESS, payload });
const getDataFailureAction = payload => ({ type: DETERMINE_USER_LOCATION_FAILURE, payload });

export default function* watcherSaga() {
  yield takeLatest(DETERMINE_USER_LOCATION, workerSaga);
}

function* workerSaga() {
  try {
    const response = yield call(() => getGeolocation());
    const location = {
      latitude: response.coords.latitude,
      longitude: response.coords.longitude
    };
    yield put(getDataSuccessAction(location));

  } catch (error) {
    yield put(getDataFailureAction(error));
  }
}

const getGeolocation = () =>
  new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(`Your browser doesn't support geolocation, app must exit`);
      return;
    };

    const locationObtained = location => resolve(location);
    const locationNotObtained = () => reject('Cannot get geolocation, app must exit');
    const geoOptions = {
      enableHighAccuracy: true,
      timeout: 60000,
      maximumAge: 0
    };

    navigator.geolocation.getCurrentPosition(locationObtained, locationNotObtained, geoOptions);
  });
