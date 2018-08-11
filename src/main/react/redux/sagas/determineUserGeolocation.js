import { call, put, takeLatest } from 'redux-saga/effects';

import {
  DETERMINE_USER_LOCATION,
  DETERMINE_USER_LOCATION_SUCCESS,
  NotificationType
} from '../../globals';

import { getShowErrorNotificationAction, getHideNotificationAction } from '../../globals/utils';

const getDataSuccessAction = payload => ({ type: DETERMINE_USER_LOCATION_SUCCESS, payload });

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
    yield put(getHideNotificationAction());
  } catch (error) {
    yield put(getShowErrorNotificationAction({ text: error, persist: true }));
  }
}

const getGeolocation = () =>
  new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(`Geolocation isn't supported. App cannot run.`);
      return;
    };

    const locationObtained = location => resolve(location);
    const locationNotObtained = () => reject('Cannot get geolocation. App will not run.');
    const geoOptions = {
      enableHighAccuracy: true,
      timeout: 60000,
      maximumAge: 0
    };

    navigator.geolocation.getCurrentPosition(locationObtained, locationNotObtained, geoOptions);
  });
