import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import createSagaMiddleware from 'redux-saga';
import { routerMiddleware } from 'react-router-redux';
import ReduxPromise from 'redux-promise';
import createHistory from 'history/createBrowserHistory';
import Promise from 'promise-polyfill';

import reducers from './redux/reducers';
import sagas from './redux/sagas';

import App from './structure/app';

const history = createHistory();
const historyMiddleware = routerMiddleware(history);
const sagaMiddleware = createSagaMiddleware();
const store = createStore(
  reducers,
  applyMiddleware(ReduxPromise, historyMiddleware, sagaMiddleware)
);

if (!window.Promise) {
  window.Promise = Promise;
}

sagaMiddleware.run(sagas);

ReactDOM.render(
  <Provider store={store}>
    <App history={history}/>
  </Provider>
  , document.getElementById('mnt'));
