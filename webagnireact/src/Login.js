import React, { Component } from 'react';
import {Link} from 'react-router-dom';
import './Login.css';
import Button from 'material-ui/Button';
import TextField from 'material-ui/TextField';
import {withStyles} from "material-ui/styles/index";
import blue from 'material-ui/colors/blue';
import grey from 'material-ui/colors/grey';
import Paper from 'material-ui/Paper';
import deepOrange from "material-ui/colors/deepOrange";

const styles = theme => ({
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        width: 600,
    },
    rightIcon:{
        marginLeft: theme.spacing.unit,
    },
    button: {
        //margin: theme.spacing.unit,
        marginLeft: 380,
        backgroundColor: blue[500],
        color: grey[50],
    },
    paper:theme.mixins.gutters({
        rounded: true,
        //margin: auto,
        width: 600,
        //border-radius: 5,
        padding: 40,
    }),
    error:{
        margin: 10,
        textAlign: 'center',
        color: deepOrange[600],
        fontSize: 15,
    },
});

class Login extends Component {
    constructor(props) {
        super(props)

        this.state = {
            username: '',
            password: '',
            submitted: false
        };
    }

    handleUsernameChange = username => event => {
        this.setState({
            [username]: event.target.value,
        });
    };

    handlePasswordChange = prop => event => {
        this.setState({ [prop]: event.target.value });
    };

    handleKeyUp = event =>{
        event.preventDefault();
        if(event.keyCode === 13){
            document.getElementById("doLogin").click();
        }
    };

    handleLogin = () => {
        console.log("Login");
        console.log(this.state.username);
        console.log(this.state.password);

        var localstorage = window.localStorage;
        var username = this.state.username;
        var password = this.state.password;
        var resultElement = document.getElementById("errorMessage");
        var informations = localstorage.getItem('token');

         var user = {
             "username": username,
             "password": password
         }

         if(informations == null){
             var xmlHttp = new XMLHttpRequest();
             xmlHttp.open( "POST", "https://liquid-layout-196103.appspot.com/rest/login");
             xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
             var myJSON = JSON.stringify(user);
             xmlHttp.send(myJSON);
             xmlHttp.onreadystatechange = function() {//Call a function when the state changes.
                 if (xmlHttp.readyState === XMLHttpRequest.DONE) {
                     if(xmlHttp.status === 200){
                         var response = xmlHttp.responseText;
                         console.log("XML response: " + response);
                         localstorage.setItem('token', response);
                         console.log(localstorage.getItem('token'));
                         console.log("Welcome");
                         //document.location.href = '/';
                     }
                     else{
                         console.log("User does not exist or password is not correct");
                         resultElement.innerHTML = "Nome de utilizador ou password incorretas";
                     }
                 }
             }
         }
         else{
             resultElement.innerHTML = "Primeiro faça logout para iniciar sessão";
         }

    }

    render() {
        const { loggingIn } = this.props;
        const { username, password, submitted } = this.state;
        const { classes } = this.props;

        return (
            <div id="login">
                <Paper className={classes.paper} style={{margin: '0 auto', backgroundColor: '#f2f2f2'}} >
                <h4>Entrar</h4>

                <div className="imgcontainer">
                    <img src={require('./img/agniNoBack.png')} alt="Avatar" width="300"/>
                </div>

                <form name="form" onSubmit={this.handleSubmit}>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !username ? ' has-error' : '')}>
                            <TextField id="username" label="Username" className={classes.textField} value={this.state.username}
                                       onChange={this.handleUsernameChange('username')}/>
                            {submitted && !username &&
                            <div className="help-block">Username is required</div>
                            }
                        </div>
                    </div><br/>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !password ? ' has-error' : '')}>
                            <TextField id="password" label="Password" type="password" className={classes.textField} value={this.state.password}
                                       onKeyUp={this.handleKeyUp} onChange={this.handlePasswordChange('password')}/>
                            {submitted && !password &&
                            <div className="help-block">Password is required</div>
                            }
                        </div>
                    </div><br/>

                    <div >
                        <Button component={Link}
                                to="/register"
                                color="primary">
                            Criar conta
                        </Button>
                        <Button id={"doLogin"} variant="raised" color={"primary"} className={classes.button} onClick={this.handleLogin}>
                            Entrar
                        </Button>
                        {loggingIn &&
                        <img alt={"dono"} src="data:image/gif;base64,R0lGODlhEAAQAPIAAP///wAAAMLCwkJCQgAAAGJiYoKCgpKSkiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAEAAQAAADMwi63P4wyklrE2MIOggZnAdOmGYJRbExwroUmcG2LmDEwnHQLVsYOd2mBzkYDAdKa+dIAAAh+QQJCgAAACwAAAAAEAAQAAADNAi63P5OjCEgG4QMu7DmikRxQlFUYDEZIGBMRVsaqHwctXXf7WEYB4Ag1xjihkMZsiUkKhIAIfkECQoAAAAsAAAAABAAEAAAAzYIujIjK8pByJDMlFYvBoVjHA70GU7xSUJhmKtwHPAKzLO9HMaoKwJZ7Rf8AYPDDzKpZBqfvwQAIfkECQoAAAAsAAAAABAAEAAAAzMIumIlK8oyhpHsnFZfhYumCYUhDAQxRIdhHBGqRoKw0R8DYlJd8z0fMDgsGo/IpHI5TAAAIfkECQoAAAAsAAAAABAAEAAAAzIIunInK0rnZBTwGPNMgQwmdsNgXGJUlIWEuR5oWUIpz8pAEAMe6TwfwyYsGo/IpFKSAAAh+QQJCgAAACwAAAAAEAAQAAADMwi6IMKQORfjdOe82p4wGccc4CEuQradylesojEMBgsUc2G7sDX3lQGBMLAJibufbSlKAAAh+QQJCgAAACwAAAAAEAAQAAADMgi63P7wCRHZnFVdmgHu2nFwlWCI3WGc3TSWhUFGxTAUkGCbtgENBMJAEJsxgMLWzpEAACH5BAkKAAAALAAAAAAQABAAAAMyCLrc/jDKSatlQtScKdceCAjDII7HcQ4EMTCpyrCuUBjCYRgHVtqlAiB1YhiCnlsRkAAAOwAAAAAAAAAAAA==" />
                        }
                    </div>
                    <div id="errorMessage" className={classes.error}></div>
                </form>
                </Paper>
            </div>
        );
    }
}

export default withStyles(styles)(Login);