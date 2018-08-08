import { call, put, take, race } from 'redux-saga/effects';
import { delay } from 'redux-saga';

import {
  LOUNGE_STATUS_POLL_START,
  LOUNGE_STATUS_POLL_STOP,
  LOUNGE_STATUS_SUCCESS,
  LOUNGE_STATUS_FAILURE
} from '../../globals';

const LOUNGE_URL = "http://localhost:3000/deviceStatus/lounge";
const getDataSuccessAction = payload => ({ type: LOUNGE_STATUS_SUCCESS, payload });
const getDataFailureAction = payload => ({ type: LOUNGE_STATUS_FAILURE, payload });

/**
 * Saga watcher.
 */
export default function* pollingWatcherSaga() {
  while (true) {
    yield take(LOUNGE_STATUS_POLL_START);
    yield race([
      call(pollingWorkerSaga),
      take(LOUNGE_STATUS_POLL_STOP)
    ]);
  }
}

/**
 * Saga worker.
 */
function* pollingWorkerSaga(action) {
  while (true) {
    try {
      const response = yield call(() => fetch(LOUNGE_URL).then(data => data.json()));
      yield put(getDataSuccessAction(response));
      yield call(delay, 2000);
    } catch (err) {
      yield put(getDataFailureAction(err));
    }
  }
}
