import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import createSagaMiddleware from 'redux-saga'
import ReduxPromise from 'redux-promise';

import reducers from './redux/reducers';
import sagas from './redux/sagas'

import App from './structure/app';

const sagaMiddleware = createSagaMiddleware();
// const createStoreWithMiddleware = applyMiddleware(ReduxPromise)(createStore);
const store = createStore(
  reducers,
  applyMiddleware(ReduxPromise, sagaMiddleware)
);

sagaMiddleware.run(sagas);

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>
  , document.getElementById('mnt'));

const pollStartAction = () => ({ type: 'POLL_START' });
// store.dispatch(pollStartAction());
