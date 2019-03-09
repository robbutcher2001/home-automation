import { call, put, take, race } from 'redux-saga/effects';
import { delay } from 'redux-saga';

import {
  LOUNGE_STATUS_API_PATH,
  LOUNGE_STATUS_POLL_START,
  LOUNGE_STATUS_POLL_STOP,
  LOUNGE_STATUS_SUCCESS,
  LOUNGE_STATUS_FAILURE
} from '../../globals';

const LOUNGE_URL = `http://localhost:3000${LOUNGE_STATUS_API_PATH}`;
const getDataSuccessAction = payload => ({ type: LOUNGE_STATUS_SUCCESS, payload });
const getDataFailureAction = payload => ({ type: LOUNGE_STATUS_FAILURE, payload });

export default function* pollingWatcherSaga() {
  while (true) {
    const { payload } = yield take(LOUNGE_STATUS_POLL_START);
    yield race([
      call(pollingWorkerSaga, payload),
      take(LOUNGE_STATUS_POLL_STOP)
    ]);
  }
}

function* pollingWorkerSaga(payload) {
  const FULL_URL = `${LOUNGE_URL}?lat=${payload.latitude}&lng=${payload.longitude}`;
  while (true) {
    try {
      const response = yield call(() => fetch(FULL_URL, { credentials: 'include' }).then(data => data.json()));
      yield put(getDataSuccessAction(response));
      yield call(delay, 2000);
    } catch (err) {
      yield put(getDataFailureAction(err)); //does this need to be error.message to show in notificationBar?
      yield call(delay, 2000);
    }
  }
}
