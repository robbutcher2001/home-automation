import { call, put, takeLatest } from 'redux-saga/effects';

import {
  START_INITIAL_STATUS_POLLER_REQUEST,
  getPathPollerMapping
} from '../../globals';

const getInitialStatusPollingAction = (type, payload) => ({ type, payload });

export default function* watcherSaga() {
  yield takeLatest(START_INITIAL_STATUS_POLLER_REQUEST, workerSaga);
}

function* workerSaga({ payload }) {
  const initialStatusPollingEndpoint = getPathPollerMapping(window.location.pathname);
  yield put(getInitialStatusPollingAction(initialStatusPollingEndpoint, payload));
}
