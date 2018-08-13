export const LANDING_PAGE_PATH = '/';
export const SECOND_PAGE_PATH = '/lounge';
export const THIRD_PAGE_PATH = '/bedrooms';
export const FOURTH_PAGE_PATH = '/other';

export const getPagePaths = () => {
  return [
    LANDING_PAGE_PATH,
    SECOND_PAGE_PATH,
    THIRD_PAGE_PATH,
    FOURTH_PAGE_PATH
  ]
};

export const VERIFY_ONLINE_REQUEST = 'VERIFY_ONLINE_REQUEST';
export const START_INITIAL_STATUS_POLLER_REQUEST = 'START_INITIAL_STATUS_POLLER_REQUEST';

export const LOGIN_REQUEST = 'LOGIN_REQUEST';

export const LOUNGE_STATUS_REQUEST = 'LOUNGE_STATUS_REQUEST';
export const LOUNGE_STATUS_POLL_START = 'LOUNGE_STATUS_POLL_START';
export const LOUNGE_STATUS_POLL_STOP = 'LOUNGE_STATUS_POLL_STOP';
export const LOUNGE_STATUS_SUCCESS = 'LOUNGE_STATUS_SUCCESS';
export const LOUNGE_STATUS_FAILURE = 'LOUNGE_STATUS_FAILURE';

export const DETERMINE_USER_LOCATION = 'DETERMINE_USER_LOCATION';
export const DETERMINE_USER_LOCATION_SUCCESS = 'DETERMINE_USER_LOCATION_SUCCESS';

export const NOTIFICATION_REQUEST = 'NOTIFICATION_REQUEST';
export const NOTIFICATION_REQUEST_SHOW = 'NOTIFICATION_REQUEST_SHOW';
export const NOTIFICATION_REQUEST_HIDE = 'NOTIFICATION_REQUEST_HIDE';
export const NOTIFICATION_BAR_DISPLAY_TIME = '3000';

export const Zones = Object.freeze({
  LOUNGE: 'lounge',
  ROBS_ROOM: 'robs_room'
});

export const NotificationType = Object.freeze({
  ERROR: 'error',
  SUCCESS: 'success'
});
