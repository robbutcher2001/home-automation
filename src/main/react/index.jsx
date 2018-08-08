import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import createSagaMiddleware from 'redux-saga'
import ReduxPromise from 'redux-promise';

import reducers from './redux/reducers';
import loungeStatusSaga from './redux/sagas/loungeStatus'

import App from './structure/app';

const sagaMiddleware = createSagaMiddleware();
// const createStoreWithMiddleware = applyMiddleware(ReduxPromise)(createStore);
const store = createStore(
  reducers,
  applyMiddleware(ReduxPromise, sagaMiddleware)
);

sagaMiddleware.run(loungeStatusSaga);

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>
  , document.getElementById('mnt'));

// const pollStartAction = () => ({ type: 'POLL_START' });
// store.dispatch({ type: 'POLL_START' });
