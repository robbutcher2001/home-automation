import { call, put, takeLatest } from 'redux-saga/effects';
import { push } from 'react-router-redux';

import {
  VERIFY_ONLINE_REQUEST,
  START_INITIAL_STATUS_POLLER_REQUEST
} from '../../globals';

import { getShowSuccessNotificationAction, getShowErrorNotificationAction } from '../../globals/utils';

const VERIFY_ONLINE_URL = 'http://localhost:3000/verifyEngineOnline';
const getStartInitialStatusPollerAction = payload => ({ type: START_INITIAL_STATUS_POLLER_REQUEST, payload });

export default function* watcherSaga() {
  yield takeLatest(VERIFY_ONLINE_REQUEST, workerSaga);
}

function* workerSaga({ payload }) {
  try {
    yield put(getShowSuccessNotificationAction({
      text: 'Contacting apartment..',
      persist: true
    }));

    const FULL_URL = `${VERIFY_ONLINE_URL}?lat=${payload.latitude}&lng=${payload.longitude}`;
    const response = yield call(() => fetch(FULL_URL, {credentials: 'include'}));

    if (response.status === 200) {
      yield put(getStartInitialStatusPollerAction(payload));
      yield put(getShowSuccessNotificationAction({
        text: 'Apartment online, polling started.',
        persist: false
      }));
    }
    else if (response.status === 406) {
      yield put(push('/login'));
      yield put(getShowErrorNotificationAction({
        text: `You're not authorised yet`,
        persist: false
      }));
    }
    else {
      throw 'Cannot reach apartment, check your connection'
    }
  } catch (error) {
    yield put(getShowErrorNotificationAction({ text: error, persist: true }));
  }
}
