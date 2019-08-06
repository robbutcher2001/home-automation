import {
  NOTIFICATION_REQUEST,
  NOTIFICATION_REQUEST_HIDE,
  NotificationType,
  LANDING_PAGE_PATH,
  SECOND_PAGE_PATH,
  THIRD_PAGE_PATH,
  FOURTH_PAGE_PATH,
  APARTMENT_STATUS_POLL_START,
  LOUNGE_STATUS_POLL_START
} from './index';

export const getShowSuccessNotificationAction = payload => {
  return {
    type: NOTIFICATION_REQUEST,
    payload: {
      text: payload.text,
      type: NotificationType.SUCCESS,
      show: true,
      persist: payload.persist
    }
  };
};

export const getShowErrorNotificationAction = payload => {
  return {
    type: NOTIFICATION_REQUEST,
    payload: {
      text: payload.text,
      type: NotificationType.ERROR,
      show: true,
      persist: payload.persist
    }
  };
};

export const getHideNotificationAction = () => ({ type: NOTIFICATION_REQUEST_HIDE });

const mapping = {};

mapping[LANDING_PAGE_PATH] = [
  APARTMENT_STATUS_POLL_START
];

mapping[SECOND_PAGE_PATH] = [
  LOUNGE_STATUS_POLL_START,
  APARTMENT_STATUS_POLL_START
];

mapping[THIRD_PAGE_PATH] = 'nothing';

mapping[FOURTH_PAGE_PATH] = 'nothing';

export const getPagePollers = path => mapping[path];
