import React, { Component } from 'react';
import {Link} from 'react-router-dom';
import './Login.css';
import Button from 'material-ui/Button';
import TextField from 'material-ui/TextField';
import SelectField from 'material-ui/Select';
import { MenuItem } from 'material-ui/Menu';
import {withStyles} from "material-ui/styles/index";
import blue from 'material-ui/colors/blue';
import grey from 'material-ui/colors/grey';
import lightGreen from 'material-ui/colors/lightGreen';
import deepOrange from 'material-ui/colors/deepOrange';
import FaceIcon from '@material-ui/icons/Face';
import Paper from 'material-ui/Paper';
import Typography from 'material-ui/Typography';
import CheckIcon from '@material-ui/icons/CheckCircle';
import CloseIcon from '@material-ui/icons/Close';

const styles = theme => ({
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        width: 550,
    },
    rightIcon:{
        marginLeft: theme.spacing.unit,
    },
    leftButton:{
        marginTop: 10,
    },
    button: {
        marginTop: 10,
        marginLeft: 380,
        backgroundColor: blue[500],
        color: grey[50],
    },
    selectRole:{
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        marginTop: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        width: 550,
    },
    paper:theme.mixins.gutters({
        width: 648,
        padding: 40,
    }),
    error:{
        margin: 10,
        textAlign: 'center',
        color: deepOrange[600],
        fontSize: 15,
        // borderRadius: 4,
        // border: '1px solid #ced4da',
        // padding: '10px 12px',
    },
    input: {
        display: 'none',
    },
    showValidation:{
        display: 'inline-block'
    },
    nonValidIcon:{
        color: deepOrange[600],
    },
    validIcon:{
        color: lightGreen[400],
    },
    helperMessage:{
        textAlign: 'center',
        color: 'grey',
        //fontFamily: 'Roboto Mono',
        fontSize: 12,
    },
});

//TODO
// function uploadFile(id) {
//     var file = document.forms["putFile"]["files"].files[0];
//     var filename = file.name;
//
//     if (filename == null || filename == "") {
//         alert("FileName is required");
//         return false;
//     } else {
//         document.forms["putFile"]["fileName"].value = filename;
//         var request = new XMLHttpRequest();
//         request.open("POST", 'https://liquid-layout-196103.appspot.com/rest/upload/' + id, false);
//         request.setRequestHeader("Content-Type", file.type);
//         request.send(file);
//     }
// }

class Register extends Component {
    constructor(props) {
        super(props)

        this.state = {
            username: '',
            name: '',
            email: '',
            role: '',
            password: '',
            confirmPass: '',
            value: 'User',
            submitted: false,
            startedUsername: true,
            startedEmail: true,
            startedName: true,
            startedPassword: true,
            startedConfPass: true,
        };

        this.handleCreateAccount = this.handleCreateAccount.bind(this);
    }

    handleUsernameChange = username => event => {
        var specialChars = /[!#$%^&*()+\-=\[\]{};':"\\|,<>\/?]+/;
        var usedSpecialChars = "@._";
        this.setState({[username]: event.target.value});

        if(event.target.value != event.target.value.toLowerCase()){
            this.setState({validUsername: false});
            document.getElementById("helperMessageUser").innerHTML = "O nome de utilizador apenas pode conter letras" +
                " minusculas";
        }

        else if(event.target.value.length < 5 || event.target.value.length > 25 || specialChars.test(event.target.value)
            || event.target.value != event.target.value.toLowerCase() ){
            //|| usedSpecialChars.indexOf(event.target.value) > 2){
            this.setState({validUsername: false});
            document.getElementById("helperMessageUser").innerHTML = "O nome de utilizador tem que conter entre " +
                "5 a 25 caracteres";
        }

        else{
            this.setState({validUsername: true});
            document.getElementById("helperMessageUser").innerHTML = "";
        }

        this.setState({startedUsername: false});

        // console.log(this.state.username);
    };

    handleNameChange = name => event => {
        var specialChars = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
        this.setState({[name]: event.target.value});

        if(event.target.value.length < 2 || event.target.value.length > 50 || specialChars.test(event.target.value)
            || event.target.value.charAt(0) == event.target.value.charAt(0).toLowerCase()){
            this.setState({validName: false});
        }
        else{
            this.setState({validName: true});
        }

        this.setState({startedName: false});
    };

    handleEmailChange = email => event => {
        var specialChars = /[!#$%^&*()+\-=\[\]{};':"\\|,<>\/?]+/;
        this.setState({ [email]: event.target.value });

        if(event.target.value.indexOf('@') <= -1 || event.target.value.length < 9 || event.target.value.length > 30
            || specialChars.test(event.target.value)){
            this.setState({validEmail: false});
        }
        else{
            this.setState({validEmail: true});
        }
        this.setState({startedEmail: false});
    };

    handlePasswordChange = password => event => {
        var specialChars = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
        this.setState({ [password]: event.target.value });

        //password tem que conter pelo menos um caracter especial, uma letra maiuscula, e ser entre 6 a 30 caracteres
        if(event.target.value.length < 6 || event.target.value.length > 30 || !specialChars.test(event.target.value)
        || event.target.value == event.target.value.toLowerCase()){
            this.setState({validPassword: false});
            document.getElementById("helperMessagePass").innerHTML = "Password tem que conter pelo menos uma letra maiuscula " +
                "e um caracter especial";
        }
        else{
            this.setState({validPassword: true});
            document.getElementById("helperMessagePass").innerHTML = "";
        }

        if(event.target.value != this.state.confirmPass || !this.state.validPassword){
            this.setState({validConfPass: false});
        }
        else{
            this.setState({validConfPass: true});
        }

        this.setState({startedPassword: false});
        console.log(event.target.value);
    };

    handleConfirmPassChange = confirmPass => event => {
        this.setState({ [confirmPass]: event.target.value });

        if(this.state.password != event.target.value || !this.state.validPassword){
            this.setState({validConfPass: false});
        }
        else{
            this.setState({validConfPass: true});
        }

        this.setState({startedConfPass: false});
    };

    handleRoleChange = value => event => {
        this.setState({ [value]: event.target.value });
    };

    handleKeyUp = event =>{
        event.preventDefault();
        if(event.keyCode === 13){
            document.getElementById("createAccount").click();
        }
    };

    //handleRoleChange = (event, index, value) => this.setState({value});

    handleCreateAccount = () => {
        // axios.post('http://localhost/rest/register/v4', {
        //     username: this.state.username,
        //     email: this.state.email,
        //     role: this.state.value,
        //     password: this.state.password,
        //     confirmPass: this.state.confirmPass,
        // }).then(function(response){
        //     document.getElementById("errorMessage").innerHTML = "Registo com sucesso";
        // }).catch(function(error){
        //     document.getElementById("errorMessage").innerHTML = "Parametros incorretos ou user ja existe";
        // })

        console.log("CreateAccount");
        console.log(this.state.username);
        console.log(this.state.name);
        console.log(this.state.email);
        console.log(this.state.value);
        console.log(this.state.password);
        console.log(this.state.confirmPass);

        console.log(this.state.validName);
        console.log(this.state.validEmail);

        if(this.state.validUsername && this.state.validName && this.state.validEmail
            && this.state.validPassword && this.state.validConfPass){
            console.log("valid informations");

            var user = {
                "username": this.state.username,
                "name": this.state.name,
                "email": this.state.email,
                "role": this.state.value,
                "password": this.state.password
            }

            console.log(user);
            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "POST", 'https://custom-tine-204615.appspot.com/rest/register');
            xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            var myJSON = JSON.stringify(user);
            xmlHttp.send(myJSON);

            xmlHttp.onreadystatechange = function() {//Call a function when the state changes.
                if(xmlHttp.readyState === XMLHttpRequest.DONE) {
                    if(xmlHttp.status === 200){
                        console.log("Sucesso");
                        var response = xmlHttp.response;
                        console.log("XML response: " + response);
                        var obj = JSON.parse(response);
                        console.log("obj");
                        console.log(obj);
                        document.getElementById("errorMessage").innerHTML = "";
                        document.getElementById("tologin").click();

                        //TODO
                        //uploadFile(obj);
                    }

                    else{
                        document.getElementById("errorMessage").innerHTML = "Parâmetros incorretos ou utilizador já existe";
                    }
                }
            }.bind(this)
        }
        else{
            document.getElementById("errorMessage").innerHTML = "Parâmetros incorretos";
        }
    };

    savePhoto = id => {
        var photo = document.getElementById("raised-button-file");
        console.log(photo);
    };

    isValid = value => {
        return false;
    };


    render() {
        const { loggingIn } = this.props;
        const { username, email, password, confirmPass, submitted, validUsername, startedUsername,
            startedEmail, startedName, startedPassword, startedConfPass, validEmail, validName, validPassword,
        validConfPass} = this.state;
        const { classes } = this.props;

        return (
            <div id="register">
                <Paper className={classes.paper} style={{margin: '0 auto', backgroundColor: '#f2f2f2'}} >
                <h4>Criar Conta</h4>

                <div className="imgcontainer">
                    <img src={require('./img/registUser2.png')} alt="Avatar2" width={100} heigth={100} />
                    <div>
                        <form encType="text/plain" method="get" name="putFile" id="putFile">
                            <div>
                                <input type="file" name="files"/>
                                <input type="hidden" name="fileName"/>
                                {/*<input*/}
                                {/*accept="image/*"*/}
                                {/*className={classes.input}*/}
                                {/*id="raised-button-file"*/}
                                {/*multiple*/}
                                {/*type="file"*/}
                                {/*/>*/}
                                {/*<label htmlFor="raised-button-file">*/}
                                {/*<Button > <FaceIcon /> Carregar Foto </Button>*/}
                                {/*component={"span"}*/}
                                {/*</label>*/}
                                {/*<input type="submit" onClick={uploadFile(this)} value="Upload Content"/>*/}
                            </div>
                        </form>

                    </div>
                </div>

                <form name="form" >
                      {/*//onSubmit={this.handleSubmit}>*/}

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !username ? ' has-error' : '')}>
                            <TextField required id="username" label="Nome de utilizador" className={classes.textField} value={this.state.username}
                                       onChange={this.handleUsernameChange('username')}/>
                            <div id={"showvalidation"} className={classes.showValidation}></div>
                            {!startedUsername && (validUsername ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}
                            {/*{!started && !valid && <div style={{margin: '0 auto'}}>O username nao pode ser vazio e apenas pode conter letras minusculas</div>}*/}
                            {/*{!this.isValid('username') && <div>Deu</div>}*/}
                            <div id="helperMessageUser" className={classes.helperMessage}></div>
                            {submitted && !username &&
                            <div className="help-block">Username is required</div>
                            }
                        </div>
                    </div>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !username ? ' has-error' : '')}>
                            <TextField required id="name" label="Name" className={classes.textField} value={this.state.name}
                                       onChange={this.handleNameChange('name')}/>
                            {!startedName &&
                            (validName ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}
                            {submitted && !username &&
                            <div className="help-block">Name is required</div>
                            }
                        </div>
                    </div>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !email ? ' has-error' : '')}>
                            <TextField required id="email" label="Email" type="email" className={classes.textField} value={this.state.email}
                                       onChange={this.handleEmailChange('email')}/>
                            {!startedEmail &&
                            (validEmail ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}
                            {submitted && !email &&
                            <div className="help-block">Email is required</div>
                            }
                        </div>
                    </div>

                    <div className={"input-group"}>
                        <SelectField value={this.state.value} onChange={this.handleRoleChange('value')} className={classes.selectRole}>
                            <MenuItem value={'User'}>User</MenuItem>
                            <MenuItem value={'GBO'}>GBO</MenuItem>
                            <MenuItem value={'GS'}>GS</MenuItem>
                            <MenuItem value={'OBE'}>OBE</MenuItem>
                        </SelectField>
                    </div>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !password ? ' has-error' : '')}>
                            <TextField required id="password" label="Password" type="password" className={classes.textField} value={this.state.password}
                                       onChange={this.handlePasswordChange('password')}/>
                            {!startedPassword &&
                            (validPassword ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}
                            <div id="helperMessagePass" className={classes.helperMessage}></div>
                            {submitted && !password &&
                            <div className="help-block">Password is required</div>
                            }
                        </div>
                    </div>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !confirmPass ? ' has-error' : '')}>
                            <TextField required id="confirmpassword" label="Confirmar Password" type="password" className={classes.textField} value={this.state.confirmPass}
                                       onKeyUp={this.handleKeyUp} onChange={this.handleConfirmPassChange('confirmPass')}/>
                            {!startedConfPass &&
                            (validConfPass ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}
                            {submitted && !confirmPass &&
                            <div className="help-block">Confirmation of password is required</div>
                            }
                        </div>
                    </div>

                    <div>
                        <Button component={Link}
                                to="/login"
                                color="primary" className={classes.leftButton}>
                            Entrar
                        </Button>
                        <Button id={"createAccount"} variant="raised" color={"primary"} className={classes.button} onClick={this.handleCreateAccount}>
                            Criar Conta
                        </Button>
                        {loggingIn &&
                        <img alt={"dono"} src="data:image/gif;base64,R0lGODlhEAAQAPIAAP///wAAAMLCwkJCQgAAAGJiYoKCgpKSkiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAEAAQAAADMwi63P4wyklrE2MIOggZnAdOmGYJRbExwroUmcG2LmDEwnHQLVsYOd2mBzkYDAdKa+dIAAAh+QQJCgAAACwAAAAAEAAQAAADNAi63P5OjCEgG4QMu7DmikRxQlFUYDEZIGBMRVsaqHwctXXf7WEYB4Ag1xjihkMZsiUkKhIAIfkECQoAAAAsAAAAABAAEAAAAzYIujIjK8pByJDMlFYvBoVjHA70GU7xSUJhmKtwHPAKzLO9HMaoKwJZ7Rf8AYPDDzKpZBqfvwQAIfkECQoAAAAsAAAAABAAEAAAAzMIumIlK8oyhpHsnFZfhYumCYUhDAQxRIdhHBGqRoKw0R8DYlJd8z0fMDgsGo/IpHI5TAAAIfkECQoAAAAsAAAAABAAEAAAAzIIunInK0rnZBTwGPNMgQwmdsNgXGJUlIWEuR5oWUIpz8pAEAMe6TwfwyYsGo/IpFKSAAAh+QQJCgAAACwAAAAAEAAQAAADMwi6IMKQORfjdOe82p4wGccc4CEuQradylesojEMBgsUc2G7sDX3lQGBMLAJibufbSlKAAAh+QQJCgAAACwAAAAAEAAQAAADMgi63P7wCRHZnFVdmgHu2nFwlWCI3WGc3TSWhUFGxTAUkGCbtgENBMJAEJsxgMLWzpEAACH5BAkKAAAALAAAAAAQABAAAAMyCLrc/jDKSatlQtScKdceCAjDII7HcQ4EMTCpyrCuUBjCYRgHVtqlAiB1YhiCnlsRkAAAOwAAAAAAAAAAAA==" />
                        }
                    </div>

                    <Typography id="errorMessage" className={classes.error} component="p"></Typography>
                </form>
                </Paper>

                <Button id={"tologin"} component={Link} to='/login' className={classes.input} color={"primary"}>
                    Sem sessao iniciada
                </Button>
            </div>
        );
    }
}

export default withStyles(styles)(Register);