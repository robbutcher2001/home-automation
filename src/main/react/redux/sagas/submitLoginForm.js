import { call, put, takeLatest } from 'redux-saga/effects';
import { goBack } from 'react-router-redux';

import { LOGIN_REQUEST } from '../../globals';

import { getShowSuccessNotificationAction, getShowErrorNotificationAction } from '../../globals/utils';

const LOGIN_URL = 'http://localhost:3000/login';

export default function* watcherSaga() {
  yield takeLatest(LOGIN_REQUEST, workerSaga);
}

function* workerSaga({ payload }) {
  try {
    yield put(getShowSuccessNotificationAction({
      text: 'Logging you in..',
      persist: true
    }));

    const response = yield call(() => fetch(LOGIN_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json; charset=utf-8',
      },
      credentials: 'include',
      body: JSON.stringify(payload)
    }));

    if (response.status === 200) {
      yield put(goBack());
      yield put(getShowSuccessNotificationAction({
        text: 'Logged in.',
        persist: false
      }));
    }
    else if (response.status === 401) {
      yield put(getShowErrorNotificationAction({
        text: 'Wrong details, try again.',
        persist: false
      }));
    }
    else {
      throw 'Cannot log you in, server may be down.';
    }
  } catch (error) {
    yield put(getShowErrorNotificationAction({ text: error.message, persist: true }));
  }
}
