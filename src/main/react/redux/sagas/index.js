import { all, fork } from 'redux-saga/effects';

import verifyEngineOnline from './verifyEngineOnline';
import submitLoginForm from './submitLoginForm';
import initialStatusPollerManager from './initialStatusPollerManager';
import determineUserGeolocation from './determineUserGeolocation';
import notificationRequest from './notificationRequest';
import loungeStatusPoller from './loungeStatusPoller';
import loungeStatus from './loungeStatus';

export default function* rootSaga() {
  yield all([
    fork(verifyEngineOnline),
    fork(submitLoginForm),
    fork(initialStatusPollerManager),
    fork(determineUserGeolocation),
    fork(notificationRequest),
    fork(loungeStatusPoller),
    fork(loungeStatus)
  ]);

  console.log('[rootSaga] App started');
}
