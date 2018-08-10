import { fork } from 'redux-saga/effects';

import determineUserGeolocation from './determineUserGeolocation';
import loungeStatus from './loungeStatus';
import loungeStatusPoller from './loungeStatusPoller';
import notificationRequest from './notificationRequest';

export default function* rootSaga() {
  yield [
    fork(determineUserGeolocation),
    fork(loungeStatus),
    fork(loungeStatusPoller),
    fork(notificationRequest)
  ];

  console.log('[rootSaga] App started');
}
