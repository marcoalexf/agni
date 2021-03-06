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
import PlacesAutocomplete from 'react-places-autocomplete';
import { geocodeByAddress, geocodeByPlaceId, getLatLng } from 'react-places-autocomplete';
import './Maps.css';
import classNames from 'classnames';

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
    accountPhoto:{
        borderRadius: 20,
        marginBottom: 20
    },
    geosuggestions:{
        fontFamily: 'Roboto Mono',
        borderColor: '#d6d7da',
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
            locality: '',
            submitted: false,
            startedUsername: true,
            startedEmail: true,
            startedName: true,
            startedPassword: true,
            startedConfPass: true,
            startedLocation: true,
            file: '',
            imagePreviewUrl: '',
            wantUploadPhoto: false,
            address: '',
        };

        this.handleCreateAccount = this.handleCreateAccount.bind(this);
        this._handleImageChange = this._handleImageChange.bind(this);
        this._handleSubmit = this._handleSubmit.bind(this);
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

        if(event.target.value.indexOf('@') <= -1 || event.target.value.length < 9 || event.target.value.length > 35
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

    handleLocalityChange = locality => event => {
        var specialChars = /[!#$%^&*()+\-=\[\]{};':"\\|,<>\/?]+/;
        this.setState({ [locality]: event.target.value });
        console.log(this.state.locality);

        // if(event.target.value.indexOf('@') <= -1 || event.target.value.length < 9 || event.target.value.length > 30
        //     || specialChars.test(event.target.value)){
        //     this.setState({validEmail: false});
        // }
        // else{
        //     this.setState({validEmail: true});
        // }
        // this.setState({startedEmail: false});
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

        // if(this.state.file != '') {
        //     this.setState({uploadPhoto: !this.state.uploadPhoto});
        //     console.log("uploadPhoto1 " + this.state.uploadPhoto);
        // }

        if(this.state.validUsername && this.state.validName && this.state.validEmail
            && this.state.validPassword && this.state.validConfPass && this.state.validLocation){
            console.log("valid informations");
            console.log("uploadPhoto1 " + this.state.uploadPhoto);

            var user = {
                "username": this.state.username,
                "name": this.state.name,
                "email": this.state.email,
                "role": this.state.value,
                "locality": this.state.locality,
                "password": this.state.password,
                "uploadPhoto": this.state.wantUploadPhoto,
            };

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

                        if(this.state.wantUploadPhoto){
                            var id = JSON.parse(response);
                            console.log("id");
                            console.log(id);
                            document.getElementById("errorMessage").innerHTML = "";

                            if(this.state.file != '')
                                this.uploadPhoto(id);

                            else
                                document.getElementById("tologin").click();
                        }

                        document.getElementById("tologin").click();
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

    uploadPhoto = id => {
        console.log("uploadPhoto");
        var file = this.state.file;
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "POST", 'https://custom-tine-204615.appspot.com/rest/upload/' + id);
        xmlHttp.setRequestHeader("Content-Type", file.type);
        // xmlHttp.setRequestHeader("Access-Control-Request-Headers", file.type);
        // var myJSON = JSON.stringify(file);
        xmlHttp.send(file);

        xmlHttp.onreadystatechange = function() {//Call a function when the state changes.
            if(xmlHttp.readyState === XMLHttpRequest.DONE) {
                if(xmlHttp.status === 200){
                    console.log("uploadPhoto sucessfully!");
                    document.getElementById("tologin").click();
                }

                else{
                    document.getElementById("errorMessage").innerHTML = "Ocorreu um erro ao carregar a sua foto";
                }
            }
        }.bind(this)

    };

    savePhoto = id => {
        var photo = document.getElementById("raised-button-file");
        console.log(photo);
    };

    isValid = value => {
        return false;
    };

    // handleSubmit(event) {
    //     event.preventDefault();
    //     // var uploadPhoto = {this.fileInput.files[0];
    //     // this.setState({photo: ${this.fileInput.files[0});
    //     alert(
    //         `Selected file - ${this.fileInput.files[0].name}`
    //     );
    // }

    // uploadFile(event){
    //     let file = event.target.files[0];
    //     console.log(file);
    //
    //     if(file){
    //         let data = new FormData();
    //         data.append('file', file);
    //     }
    //
    // }

//     uploadFile() {
//         var file = this.refs.file.getDOMNode().files[0];
//         var reader = new FileReader();
//         reader.onload = function(output){
//             fileUpload.set({
//                 file: output.target.result
//             });
//             $.when(fileUpload.save())
//                 .done(function(){
//                     this.setState({
//                         uploaded: true
//                     });
//                 }.bind(this));
//         }
//
//     reader.readAsDataURL(file);
// }

    _handleSubmit(e) {
        e.preventDefault();
        // TODO: do something with -> this.state.file
    }

    _handleImageChange(e) {
        e.preventDefault();

        let reader = new FileReader();
        let file = e.target.files[0];

        if(file){
            reader.onloadend = () => {
                this.setState({
                    file: file,
                    imagePreviewUrl: reader.result,
                    wantUploadPhoto: true,
                });
            };

            reader.readAsDataURL(file)
        }
    }

    handleAddressChange = (address) => {
        this.setState({address});
        this.setState({locality: ''});
        this.setState({startedLocation: false});
        this.setState({validLocation: false});
    };

    handleAddressSelect = (address) => {
        geocodeByAddress(address)
            .then(results => getLatLng(results[0]))
            .then(latLng => console.log('Success', latLng))
            .catch(error => console.error('Error', error));

        this.setState({address});
        this.setState({ locality: address });
        this.setState({validLocation: true});
        console.log("locality: " + this.state.locality);
    };

    render() {
        const { loggingIn } = this.props;
        const { username, name, email, password, locality, confirmPass, submitted,
            validUsername, validEmail, validName, validPassword, validConfPass, validLocation} = this.state;
        const { classes } = this.props;
        let {imagePreviewUrl} = this.state;
        let $imagePreview = (<img src={require('./img/registUser2.png')} alt="Avatar2" width={100} style={{marginBottom: '20'}} />);
        if (imagePreviewUrl) {
            $imagePreview = (<img src={imagePreviewUrl} width={200} className={classes.accountPhoto} />);
        }

        return (
            <div id="register">
                <Paper className={classes.paper} style={{margin: '0 auto', backgroundColor: '#f2f2f2'}} >
                <h4>Criar Conta</h4>

                <div className="imgcontainer">
                    {/*<img src={require('./img/registUser2.png')} alt="Avatar2" width={100} heigth={100} />*/}
                    <div>
                        {/*<form encType="text/plain" method="get" name="putFile" id="putFile">*/}
                            {/*<div>*/}
                                {/*<input type="file" name="files"/>*/}
                                {/*<input type="hidden" name="fileName"/>*/}
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
                            {/*</div>*/}
                        {/*</form>*/}
                        {/*<form onSubmit={this.uploadFile}>*/}
                            {/*<label>*/}
                                {/*Carregar Foto:*/}
                                {/*<input*/}
                                    {/*type="file"*/}
                                    {/*name="file"*/}
                                    {/*ref={input => {*/}
                                        {/*this.fileInput = input;*/}
                                    {/*}}*/}
                                    {/*onChange={this.uploadFile}*/}
                                {/*/>*/}
                            {/*</label>*/}
                            {/*<br />*/}
                            {/*<button type="submit">Submit</button>*/}
                        {/*</form>*/}
                        {$imagePreview}
                        <form onSubmit={this._handleSubmit}>
                            <input type="file" onChange={this._handleImageChange} />
                            {/*<button type="submit" onClick={this._handleSubmit}>Upload Image</button>*/}
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
                            {username.length != 0 &&
                            (validUsername ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}
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
                            {name.length != 0 &&
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
                            {email.length != 0 &&
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

                    <div>
                        {/*<div className={'form-group' + (submitted && !locality ? ' has-error' : '')}>*/}
                            {/*<TextField required id="locality" label="Localidade" className={classes.textField} value={this.state.locality}*/}
                                       {/*onChange={this.handleLocalityChange('locality')}/>*/}
                            {/*/!*{!startedEmail &&*!/*/}
                            {/*/!*(validEmail ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}*!/*/}
                            {/*/!*{submitted && !locality &&*!/*/}
                            {/*/!*<div className="help-block">Localidade is required</div>*!/*/}
                            {/*/!*}*!/*/}
                        {/*</div>*/}

                        <PlacesAutocomplete
                            value={this.state.address}
                            onChange={this.handleAddressChange}
                            onSelect={this.handleAddressSelect}
                            //className="pac-container"
                        >
                            {({ getInputProps, suggestions, getSuggestionItemProps }) => {
                                return(
                                    <div>
                                        <div className={"search-input-container"}>
                                            <TextField label={'Localidade'}
                                                       {...getInputProps({
                                                           //placeholder: 'Localidade',
                                                           //className: 'search-input'
                                                       })}
                                                className={classes.textField}
                                            />
                                            {this.state.address.length != 0 &&
                                            (validLocation ? <CheckIcon className={classes.validIcon}/> : <CloseIcon className={classes.nonValidIcon}/> )}
                                        </div>

                                        {suggestions.length> 0 &&

                                        (<div className='autocomplete-container'>
                                                {suggestions.map(suggestion => {
                                                    const className = classNames(
                                                        'suggestion-item'
                                                        , {
                                                        'suggestion-item--active': suggestion.active,
                                                    })
                                                    ;
                                                    //suggestion.active ? 'suggestion-item--active' : 'suggestion-item';
                                                    // inline style for demonstration purpose
                                                    // const style = suggestion.active
                                                    //     ? { backgroundColor: '#d3d3d3', cursor: 'pointer' }
                                                    //     : { backgroundColor: '#ffffff', cursor: 'pointer' };
                                                    return (
                                                        <div {...getSuggestionItemProps(suggestion, { className })}>
                                                            <strong>
                                                                {suggestion.formattedSuggestion.mainText}
                                                            </strong>{' '}
                                                            <small>
                                                                {suggestion.formattedSuggestion.secondaryText}
                                                            </small>
                                                        </div>
                                                    );
                                                })}
                                                <div className="dropdown-footer">
                                                    <div>
                                                        <img
                                                            src={require('./img/powered_by_google_default.png')}
                                                            className="dropdown-footer-image"
                                                        />
                                                    </div>
                                                </div>
                                            </div>
                                        )}
                                        <div id="helperMessageLocation" className={classes.helperMessage}></div>
                                    </div>
                                    );
                                    }}
                    </PlacesAutocomplete>

                        {/*<div className="pac-container pac-logo"*/}
                             {/*style={{width: 557, position: 'absolute', left: 66, top: 106, display: 'none',}}>*/}
                            {/*<div className="pac-item">*/}
                                {/*<span className="pac-icon pac-icon-marker"></span>*/}
                                {/*<span className="pac-item-query">*/}
                                    {/*/!*<span>France</span>*!/*/}
                                {/*</span>*/}
                            {/*</div>*/}
                        {/*</div>*/}
                    </div>

                    <div className="input-group">
                        <div className={'form-group' + (submitted && !password ? ' has-error' : '')}>
                            <TextField required id="password" label="Password" type="password" className={classes.textField} value={this.state.password}
                                       onChange={this.handlePasswordChange('password')}/>
                            {password.length != 0 &&
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
                            {confirmPass.length != 0 &&
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
                    IniciarSessao
                </Button>
            </div>
        );
    }
}

export default withStyles(styles)(Register);