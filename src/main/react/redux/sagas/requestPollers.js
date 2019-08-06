import { call, put, takeLatest } from 'redux-saga/effects';

import { REQUEST_POLLERS } from '../../globals';

const getPollerTriggerAction = (type, payload) => ({ type, payload });

export default function* watcherSaga() {
  yield takeLatest(REQUEST_POLLERS, workerSaga);
}

function* workerSaga({ payload }) {
  if (payload.requestedPollers) {
    yield all(payload.requestedPollers.map(poller => {
      console.log(`Requesting ${poller}`);
      put(getPollerTriggerAction(poller, payload));
    }));
  }
}
