import { call, put, takeLatest } from 'redux-saga/effects';

import {
  LOUNGE_STATUS_REQUEST,
  LOUNGE_STATUS_SUCCESS,
  LOUNGE_STATUS_FAILURE
} from '../../globals';

const LOUNGE_URL = 'http://localhost:3000/deviceStatus/lounge';
const getDataSuccessAction = payload => ({ type: LOUNGE_STATUS_SUCCESS, payload });
const getDataFailureAction = payload => ({ type: LOUNGE_STATUS_FAILURE, payload });

export default function* watcherSaga() {
  yield takeLatest(LOUNGE_STATUS_REQUEST, workerSaga);
}

function* workerSaga({ payload }) {
  console.log(payload.hello);
  try {
    const response = yield call(() => fetch(LOUNGE_URL, { credentials: 'include' }).then(data => data.json()));
    yield put(getDataSuccessAction(response));

  } catch (error) {
    yield put(getDataFailureAction(error));
  }
}
