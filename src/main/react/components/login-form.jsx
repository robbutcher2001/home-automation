import React, { Component } from 'react';
import { connect } from 'react-redux';

import { LOGIN_REQUEST } from '../globals';

class LoginForm extends Component {
  constructor(props) {
    super(props);

    this.state = {
      uname: '',
      pword: ''
    };

    this.onNameChange = this.onNameChange.bind(this);
    this.onPasswordChange = this.onPasswordChange.bind(this);
    this.onHandleSubmit = this.onHandleSubmit.bind(this);
  }

  onNameChange(userInput) {
    this.setState(prevState => ({
      uname: userInput,
      pword: prevState.pword
    }));
  }

  onPasswordChange(userInput) {
    this.setState(prevState => ({
      uname: prevState.uname,
      pword: userInput
    }));
  }

  onHandleSubmit(event) {
    event.preventDefault();
    this.props.fireLoginRequest(this.state);
  }

  render() {
    return (
      <article id='contact' className='panel'>
        <header>
          <h2>Login to continue</h2>
        </header>
        <form onSubmit={event => this.onHandleSubmit(event)}>
          <div>
            <div className='row'>
              <div className='col-12'>
                <input
                  type='text'
                  name='uname'
                  placeholder='Name'
                  value={this.state.uname}
                  onChange={event => this.onNameChange(event.target.value)}
                />
              </div>
              <div className='col-12'>
                <input
                  type='password'
                  name='pword'
                  placeholder='Password'
                  value={this.state.pword}
                  onChange={event => this.onPasswordChange(event.target.value)}
                />
              </div>
              <div className='col-12'>
                <input type='submit' value='Login (3 months)' />
              </div>
            </div>
          </div>
        </form>
      </article>
    );
  }
}

const mapDispatchToProps = dispatch => {
  return {
    fireLoginRequest: payload => dispatch({ type: LOGIN_REQUEST, payload })
  };
};

export default connect(null, mapDispatchToProps)(LoginForm);
