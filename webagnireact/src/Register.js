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
import FaceIcon from '@material-ui/icons/Face';
import Paper from 'material-ui/Paper';
import Typography from 'material-ui/Typography';

const styles = theme => ({
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        width: 600,
    },
    rightIcon:{
        marginLeft: theme.spacing.unit,
    },
    leftButton:{
        flex: 1,
        margin: theme.spacing.unit,
        justifyContent: 'flex-start'
    },
    button: {
        margin: theme.spacing.unit,
        backgroundColor: blue[500],
        color: grey[50],
    },
    buttons:{
        flexGrow: 1,
        //height: 430,
        //zIndex: 1,
        //overflow: 'hidden',
        //position: 'relative',
        display: 'flex',
    },
    selectRole:{
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        marginTop: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        width: 600,
    },
    paper:theme.mixins.gutters({
        width: 600,
        padding: 40,
    }),
    error:{
        textAlign: 'center',
        color: 'red',
    },
});

class Register extends Component {
    constructor(props) {
        super(props)


        this.state = {
            username: '',
            email: '',
            role: '',
            password: '',
            confirmPass: '',
            value: 'User',
            submitted: false,
        };
    }

    handleUsernameChange = username => event => {
        this.setState({[username]: event.target.value,});
    };

    handleEmailChange = email => event => {
        this.setState({ [email]: event.target.value });
    };

    handlePasswordChange = password => event => {
        this.setState({ [password]: event.target.value });
    };

    handleConfirmPassChange = confirmPass => event => {
        this.setState({ [confirmPass]: event.target.value });
    };

    handleRoleChange = value => event => {
        this.setState({ [value]: event.target.value });
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
        console.log(this.state.email);
        console.log(this.state.value);
        console.log(this.state.password);
        console.log(this.state.confirmPass);

        var user = {
            "username": this.state.username,
            "email": this.state.email,
            "role": this.state.value,
            "password": this.state.password,
            "confirmation": this.state.confirmPass
        }

        console.log(user);
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "POST", 'http://localhost:8080/rest/register/v4');
        xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        var myJSON = JSON.stringify(user);
        xmlHttp.send(myJSON);
        console.log("XML response:" + xmlHttp.responseText);

        xmlHttp.onreadystatechange = function() {//Call a function when the state changes.
            if(xmlHttp.readyState == XMLHttpRequest.DONE) {

                if(xmlHttp.status == 200){
                    console.log("Sucesso");
                    document.getElementById("errorMessage").innerHTML = "";
                    document.location.href = '/login';
                }

                else{
                    document.getElementById("errorMessage").innerHTML = "Parâmetros incorretos ou utilizador já existe";
                }
            }

        }
    }


    render() {
        const { loggingIn } = this.props;
        const { username, email, value, password, confirmPass, submitted } = this.state;
        const { classes } = this.props;

        return (
            <div id="login">
                <Paper className={classes.paper} style={{margin: '0 auto', backgroundColor: '#f2f2f2'}} >
                <h4>Criar Conta</h4>

                <div className="imgcontainer">
                    <img src={require('./img/registUser2.png')} alt="Avatar2" width={100} heigth={100} />
                    <div><Button> <FaceIcon /> Carregar Foto </Button></div>
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
                    </div>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !email ? ' has-error' : '')}>
                            <TextField id="email" label="Email" type="email" className={classes.textField} value={this.state.email}
                                       onChange={this.handleEmailChange('email')}/>
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
                            <TextField id="password" label="Password" type="password" className={classes.textField} value={this.state.password}
                                       onChange={this.handlePasswordChange('password')}/>
                            {submitted && !password &&
                            <div className="help-block">Password is required</div>
                            }
                        </div>
                    </div>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !confirmPass ? ' has-error' : '')}>
                            <TextField id="confirmpassword" label="Reentroduza a Password" type="password" className={classes.textField} value={this.state.confirmPass}
                                       onChange={this.handleConfirmPassChange('confirmPass')}/>
                            {submitted && !confirmPass &&
                            <div className="help-block">Confirmation of password is required</div>
                            }
                        </div>
                    </div>

                    <div className={classes.buttons}>
                        <Button component={Link}
                                to="/login"
                                color="primary" className={classes.leftButton}>
                            Entrar
                        </Button>
                        <Button variant="raised" color={"primary"} className={classes.button} onClick={this.handleCreateAccount}>
                            Criar Conta
                        </Button>
                        {loggingIn &&
                        <img alt={"dono"} src="data:image/gif;base64,R0lGODlhEAAQAPIAAP///wAAAMLCwkJCQgAAAGJiYoKCgpKSkiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAEAAQAAADMwi63P4wyklrE2MIOggZnAdOmGYJRbExwroUmcG2LmDEwnHQLVsYOd2mBzkYDAdKa+dIAAAh+QQJCgAAACwAAAAAEAAQAAADNAi63P5OjCEgG4QMu7DmikRxQlFUYDEZIGBMRVsaqHwctXXf7WEYB4Ag1xjihkMZsiUkKhIAIfkECQoAAAAsAAAAABAAEAAAAzYIujIjK8pByJDMlFYvBoVjHA70GU7xSUJhmKtwHPAKzLO9HMaoKwJZ7Rf8AYPDDzKpZBqfvwQAIfkECQoAAAAsAAAAABAAEAAAAzMIumIlK8oyhpHsnFZfhYumCYUhDAQxRIdhHBGqRoKw0R8DYlJd8z0fMDgsGo/IpHI5TAAAIfkECQoAAAAsAAAAABAAEAAAAzIIunInK0rnZBTwGPNMgQwmdsNgXGJUlIWEuR5oWUIpz8pAEAMe6TwfwyYsGo/IpFKSAAAh+QQJCgAAACwAAAAAEAAQAAADMwi6IMKQORfjdOe82p4wGccc4CEuQradylesojEMBgsUc2G7sDX3lQGBMLAJibufbSlKAAAh+QQJCgAAACwAAAAAEAAQAAADMgi63P7wCRHZnFVdmgHu2nFwlWCI3WGc3TSWhUFGxTAUkGCbtgENBMJAEJsxgMLWzpEAACH5BAkKAAAALAAAAAAQABAAAAMyCLrc/jDKSatlQtScKdceCAjDII7HcQ4EMTCpyrCuUBjCYRgHVtqlAiB1YhiCnlsRkAAAOwAAAAAAAAAAAA==" />
                        }
                    </div>

                    <Typography id="errorMessage" className={classes.error} component="p"></Typography>
                </form>
                </Paper>
            </div>
        );
    }
}

export default withStyles(styles)(Register);