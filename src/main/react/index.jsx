import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore, applyMiddleware } from 'redux';
import ReduxPromise from 'redux-promise';

import reducers from './redux/reducers';
const createStoreWithMiddleware = applyMiddleware(ReduxPromise)(createStore);

import Homepage from './structure/homepage';

ReactDOM.render(
  <Provider store={createStoreWithMiddleware(reducers)}>
    <Homepage />
  </Provider>
  , document.getElementById('mnt'));
