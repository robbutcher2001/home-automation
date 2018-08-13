import React from 'react';

import Nav from '../components/nav';
import LoginForm from '../components/login-form';
import Footer from '../components/footer';

const LoginPage = () => {
  return (
    <div id='wrapper'>
      <div id='main'>
        <LoginForm />
      </div>
      <Footer />
    </div>
  );
}

export default LoginPage;
