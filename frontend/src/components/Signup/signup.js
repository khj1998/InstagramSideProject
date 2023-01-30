import React,{useState,useEffect} from 'react';
import Grid  from '@material-ui/core/Grid';
import {Link} from 'react-router-dom';
import axios from 'axios';
import Logo from '../../images/logo.png'
import instagram_img from '../../images/9364675fb26a.svg';
import instagram_logo from '../../images/logoinsta.png';
import facebook_img from '../../images/fb.png';
import './signup.scss'

const Signup = () => {

    return (
    <div className="container">
          <Grid container>

            <Grid item xs={5}>
             </Grid>

             <Grid item xs={3}>
             <div className="loginpage-component">
                            <img className="loginpage-logo" src={instagram_logo} />

                            <div className="instagram-intro">
                            친구들의 사진과 동영상을 보려면<br/> 가입하세요.
                            </div>

                            <button type="submit" className="login-button">
                            <div className="login-fb2">
                                 <img src={facebook_img} width="15px" style={{"marginRight":"5px"}} />페이스북으로 로그인
                            </div>
                            </button>

                            <div className="login-ordiv">
                                <div className="divide-left"></div>
                                <div className="login-or">또는</div>
                                <div className="divide-right"></div>
                            </div>

                            <form onSubmit = {(e) => onLogIn(e)}>
                            <div className="signuppage-signup">
                                 <input
                                    type="text"
                                    name="email"
                                    className="signuppage-text"
                                    placeholder="이메일 주소"
                                    onChange={(e) => onInputChange(e)}/>

                                 <input
                                  type="text"
                                  name="email"
                                  className="signuppage-text"
                                  placeholder="성명"
                                  onChange={(e) => onInputChange(e)}/>

                                 <input
                                  type="text"
                                  name="email"
                                  className="signuppage-text"
                                  placeholder="사용자 이름"
                                  onChange={(e) => onInputChange(e)}/>

                                 <input
                                    type="password"
                                    name="password"
                                    className="signuppage-text"
                                    placeholder="비밀번호"
                                    onChange={(e) => onInputChange(e)}/>
                                 <button type="submit" className="signup-button">가입</button>
                            </div>
                            </form>

                            <div className="login-forgot">
                                 <a href="#">비밀번호를 잊으셨나요?</a>
                            </div>
                        </div>
             </Grid>
          </Grid>
        </div>
    );
};

export default Signup;