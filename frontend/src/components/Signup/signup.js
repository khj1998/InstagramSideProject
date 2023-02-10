import React,{useState,useEffect} from 'react';
import Grid  from '@material-ui/core/Grid';
import {Link} from 'react-router-dom';
import axios from 'axios';
import Logo from '../../images/logo.png'
import instagram_img from '../../images/9364675fb26a.svg';
import instagram_logo from '../../images/logoinsta.png';
import facebook_img from '../../images/fb.png';
import './signup.scss'
import { async } from 'q';

const Signup = () => {

    const [user, setUser] = useState({
            nickname:'',
            email:'',
            password:'',
            passwordChecker:''
      });

      const {nickname,email,password,passwordChecker} = user;

      const onInputChange = (e) => {
          setUser({...user,[e.target.name]:e.target.value});
      }

      const onSignUp = async (e) => {
              e.preventDefault();
              await axios.post(`http://localhost:8080/users/add`,user
                ,{
                    withCredentials : true,
                    headers : {'Content-Type': 'application/json'}
                })
                .then((response) => {
                    console.log(response);
                });
      };

    return (
    <div className="container">
          <Grid container>

            <Grid item xs={5}>
             </Grid>

             <Grid item xs={3}>
             <div className="signuppage-component">
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

                            <form>
                            <div className="signuppage-signup">

                                <input
                                   type="text"
                                   name="nickname"
                                   className="signuppage-text"
                                   placeholder="사용할 닉네임"
                                   onChange={(e) => onInputChange(e)}/>

                                 <input
                                    type="text"
                                    name="email"
                                    className="signuppage-text"
                                    placeholder="이메일 주소"
                                    onChange={(e) => onInputChange(e)}/>

                                 <input
                                    type="password"
                                    name="password"
                                    className="signuppage-text"
                                    placeholder="비밀번호"
                                    onChange={(e) => onInputChange(e)}/>

                                 <input
                                    type="password"
                                    name="passwordChecker"
                                    className="signuppage-text"
                                    placeholder="비밀번호 확인"
                                    onChange={(e) => onInputChange(e)}/>

                                 <button type="button" onClick = {(e) => onSignUp(e)} className="signup-button">가입</button>
                            </div>
                            </form>

                            <div className="instagram-intro">
                                가입하면 Instagram의 약관, 데이터<br/>정책 및 쿠키 정책에 동의하게 됩니<br/>다.
                            </div>
                        </div>
             <div className="signuppage-bottom">
                <div className="login-account">
                     계정이 있으신가요? <Link to ="/">로그인</Link>
                </div>
             </div>
             </Grid>
          </Grid>
        </div>
    );
};

export default Signup;