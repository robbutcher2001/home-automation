import { fork } from 'redux-saga/effects';

import determineUserGeolocation from './determineUserGeolocation';
import loungeStatus from './loungeStatus';
import loungeStatusPoller from './loungeStatusPoller';

export default function* rootSaga() {
  yield [
    fork(determineUserGeolocation),
    fork(loungeStatus),
    fork(loungeStatusPoller)
  ];

  console.log('[rootSaga] App started');
}
