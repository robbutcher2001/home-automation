import { call, put, takeLatest } from 'redux-saga/effects';
import { delay } from 'redux-saga';

import {
  NOTIFICATION_REQUEST,
  NOTIFICATION_REQUEST_SHOW,
  NOTIFICATION_REQUEST_HIDE
} from '../../globals';

const getShowAction = payload => ({ type: NOTIFICATION_REQUEST_SHOW, payload });
const getHideAction = payload => ({ type: NOTIFICATION_REQUEST_HIDE, payload });

export default function* watcherSaga() {
  yield takeLatest(NOTIFICATION_REQUEST, workerSaga);
}

function* workerSaga() {
  const payload = {
    notificationBar: {
      text: 'Hello, there.',
      show: true
    }
  };
  yield put(getShowAction(payload));
  yield call(delay, 2000);

  const resetPayload = {
    notificationBar: {}
  };
  yield put(getHideAction(resetPayload));
}
