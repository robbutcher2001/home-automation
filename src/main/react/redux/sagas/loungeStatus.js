import { call, put, takeLatest } from 'redux-saga/effects';

import {
  LOUNGE_STATUS_REQUEST,
  LOUNGE_STATUS_SUCCESS,
  LOUNGE_STATUS_FAILURE
} from '../../globals';

const LOUNGE_URL = "http://localhost:3000/deviceStatus/lounge";
const getDataSuccessAction = payload => ({ type: LOUNGE_STATUS_SUCCESS, payload });
const getDataFailureAction = payload => ({ type: LOUNGE_STATUS_FAILURE, payload });

export default function* watcherSaga() {
  yield takeLatest(LOUNGE_STATUS_REQUEST, workerSaga);
}

// worker saga: makes the api call when watcher saga sees the action
function* workerSaga({ payload }) {
  console.log(payload.hello);
  try {
    const response = yield call(() => fetch(LOUNGE_URL).then(data => data.json()));
    // dispatch a success action to the store with the new dog
    yield put(getDataSuccessAction(response));

  } catch (error) {
    // dispatch a failure action to the store with the error
    yield put(getDataFailureAction(error));
  }
}
