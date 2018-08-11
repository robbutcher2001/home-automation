import { call, put, takeLatest } from 'redux-saga/effects';
import { delay } from 'redux-saga';

import {
  NOTIFICATION_REQUEST,
  NOTIFICATION_REQUEST_SHOW,
  NOTIFICATION_BAR_DISPLAY_TIME
} from '../../globals';

import { getHideNotificationAction } from '../../globals/utils';

const getShowNotificationAction = payload => ({ type: NOTIFICATION_REQUEST_SHOW, payload });

export default function* watcherSaga() {
  yield takeLatest(NOTIFICATION_REQUEST, workerSaga);
}

function* workerSaga({ payload }) {
  yield put(getShowNotificationAction(payload));

  if (!payload.persist) {
    yield call(delay, NOTIFICATION_BAR_DISPLAY_TIME);
    yield put(getHideNotificationAction());
  }
}
