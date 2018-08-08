// import axios from 'axios';
import { call, put, take, race } from 'redux-saga/effects';

export const POLL_START = 'POLL_START';
export const POLL_STOP = 'POLL_STOP';
export const GET_DATA_SUCCESS = 'GET_DATA_SUCCESS';
export const GET_DATA_FAILURE = 'GET_DATA_FAILURE';

const getDataSuccessAction = payload => ({ type: GET_DATA_SUCCESS, payload });
const getDataFailureAction = payload => ({ type: GET_DATA_FAILURE, payload });
const LOUNGE_URL = "http://localhost:3000/deviceStatus/lounge";

/**
 * Saga worker.
 */
function* pollSagaWorker(action) {
  while (true) {
    try {
      const { data } = yield call(() => fetch(LOUNGE_URL).then(data => data.json()));
      yield put(getDataSuccessAction(data));
      yield call(delay, 4000);
    } catch (err) {
      yield put(getDataFailureAction(err));
    }
  }
}

/**
 * Saga watcher.
 */
export default function* pollSagaWatcher() {
  while (true) {
    yield take(POLL_START);
    yield race([
      call(pollSagaWorker),
      take(POLL_STOP)
    ]);
  }
}
