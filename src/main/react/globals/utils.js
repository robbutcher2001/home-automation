import { NOTIFICATION_REQUEST, NOTIFICATION_REQUEST_HIDE, NotificationType } from './index';

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
      type: NotificationType.WARNING,
      show: true,
      persist: payload.persist
    }
  };
};

export const getHideNotificationAction = () => ({ type: NOTIFICATION_REQUEST_HIDE });
