import React from 'react';
import PropTypes from 'prop-types';
import Typography from 'material-ui/Typography';
import { withStyles } from 'material-ui/styles';
import { Manager, Target, Popper } from 'react-popper';
import Paper from 'material-ui/Paper';
import Button from 'material-ui/Button';
import IconButton from 'material-ui/IconButton';
import PhotoIcon from '@material-ui/icons/Photo';
import ProfileIcon from '@material-ui/icons/Create';
import SettingsIcon from '@material-ui/icons/Settings';
import ClickAwayListener from 'material-ui/utils/ClickAwayListener';
import Grow from 'material-ui/transitions/Grow';
import { MenuItem, MenuList } from 'material-ui/Menu';
import classNames from 'classnames';
import FavoriteIcon from '@material-ui/icons/Favorite';
import ReportsIcon from '@material-ui/icons/ViewList';
import PersonPinIcon from '@material-ui/icons/PersonPin';
import CheckIcon from '@material-ui/icons/Check';
import Tabs, { Tab } from 'material-ui/Tabs';
import Avatar from 'material-ui/Avatar';
import {Link, Redirect} from 'react-router-dom';
import Dialog, {DialogActions, DialogContent, DialogContentText, DialogTitle,} from 'material-ui/Dialog';
import TextField from 'material-ui/TextField';
import SwipeableViews from 'react-swipeable-views';
import img2 from './img/news2.jpg';
import List from 'material-ui/List';

function TabContainer(props) {
    return (
        <Typography component="div" style={{ padding: 8 * 3 }}>
            {props.children}
        </Typography>
    );
}

const styles =  theme => ({
    title:{
        margin:20,
    },
    media: {
        marginLeft: 40,
    },
    paper:theme.mixins.gutters({
        width: 800,
        padding: 40,
        height: 400,
    }),
    paperTwo:theme.mixins.gutters({
        width: 800,
        padding: 40,
    }),
    editProfile:{
        marginLeft: 500,
        marginBottom: 20,
    },
    settings: {
        display: 'inline-block',
        marginLeft: 20,
        marginBottom: 20,
    },
    popperClose: {
        pointerEvents: 'none',
    },
    informations:{
        display: 'inline-block',
        marginLeft: 80,
        top: 250,
        // left: 850,
    },
    username:{
        fontFamily: 'Montserrat',
        fontSize: 48,
    },
    basicInfo:{
        display: 'inline-block',
        marginRight: 20,
        marginTop: 20,
        marginBottom: 20,
        fontSize: 15,
    },
    role:{
        fontFamily: 'Montserrat',
        fontSize: 20,
        //fontStyle: 'thin',
        color: 'grey',
        //textAlign: 'center',
    },
    avatar:{
        margin: 20,
        // width: 200,
        // heigth: 200,
    },
    textField:{
        margin: 10,
        width: 400,
    },
    dialogProfile:{

    },
    row: {
        display: 'flex',
        justifyContent: 'center',
    },
    bigAvatar: {
        width: 80,
        height: 80,
    },
    biggerAvatar: {
        width: 180,
        height: 180,
        fontSize: 90,
    },
    input: {
        display: 'none',
    },
    img:{
      width: 200,
    },
});

let informations = new Promise(function(resolve, reject) {
    console.log("informations1");
    var obj;
    var token = window.localStorage.getItem('token');
    var d = new Date();
    var t = d.getTime();
    // var expirationData = JSON.parse(token).expirationData;
    // console.log(token);
    // console.log(t);
    // console.log(expirationData);

    if(token != null){
        var uname = JSON.parse(token).username;
        var tokenObj = JSON.parse(token);

        var user = {
            "username": uname,
            "token": tokenObj
        }

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "POST", "https://custom-tine-204615.appspot.com/rest/profile/", true);
        xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        var myJSON = JSON.stringify(user);
        xmlHttp.send(myJSON);

        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState === XMLHttpRequest.DONE){
                if(xmlHttp.status === 200){
                    console.log("informations2");
                    var response = xmlHttp.response;
                    console.log("XML response: " + response);
                    obj = JSON.parse(response);
                    console.log(obj);

                    console.log(uname.charAt(0));

                    resolve(obj);
                }

                else{
                    console.log("tempo expirado");
                    window.localStorage.removeItem('token');
                    // document.getElementById("tologin").click();
                }
            }
        }.bind(this)
    }
    else{
        console.log("Sem sessao iniciada");
        // document.getElementById("tologin").click();

    }
}.bind(this));

let xmlRequest = new Promise(function(resolve, reject) {
    console.log("xmlRequest");
    // var t = true;
    var token = window.localStorage.getItem('token');
    var tokenObj = JSON.parse(token);
    if(token != null){
        var uname = JSON.parse(token).username;
        var map;

        var user = {
            "username": uname,
            "token": tokenObj,
            "showPrivate": true //MUDAR ISTO DEPOIS
        };

        console.log("pedido");
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("POST", "https://custom-tine-204615.appspot.com/rest/occurrence/list", true);
        xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        var myJSON = JSON.stringify(user);
        xmlHttp.send(myJSON);

        console.log("esperar pelo estado");
        xmlHttp.onreadystatechange = function () {
            console.log("1");
            if (xmlHttp.readyState === 4) {
                console.log("2");
                if (xmlHttp.status === 200) {
                    console.log("3");
                    var response = xmlHttp.response;
                    var obj = JSON.parse(response);
                    console.log("obj:");
                    console.log(obj);
                    map = obj[0];
                    console.log("map:");
                    console.log(map);

                    resolve(obj.mapList);
                }
                else {
                    console.log("tempo expirado");
                    window.localStorage.removeItem('token');
                }
            }
        }.bind(this)
    }
    else{
        console.log("Sem sessao iniciada");
    }
}.bind(this));

class Profile extends React.Component {
    constructor(props){
        super(props);

        this.loadInformations = this.loadInformations.bind(this);
    }

    state = {
        accountOpen: false,
        value: 0,
        editProfile: false,
        editPass: false,
        isLoggedIn: true,
        username: '',
        email: '',
        name: '',
        role: '',
        hasReports: false,
        reports: [
            {user_occurrence_title: ''}],
        hasPhoto: true,
        s: true,
    };

    loadInformations = () =>{
        var obj;
        var token = window.localStorage.getItem('token');
        var d = new Date();
        var t = d.getTime();
        // var expirationData = JSON.parse(token).expirationData;
        // console.log(token);
        // console.log(t);
        // console.log(expirationData);

        if(token != null){
            var uname = JSON.parse(token).username;
            var tokenObj = JSON.parse(token);

            var user = {
                "username": uname,
                "token": tokenObj
            }

            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "POST", "https://custom-tine-204615.appspot.com/rest/profile/", true);
            xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            var myJSON = JSON.stringify(user);
            xmlHttp.send(myJSON);

            xmlHttp.onreadystatechange = function() {
                if (xmlHttp.readyState === XMLHttpRequest.DONE){
                    if(xmlHttp.status === 200){
                        var response = xmlHttp.response;
                        console.log("XML response: " + response);
                        obj = JSON.parse(response);

                        var username = document.getElementById("showusername");
                        username.innerHTML = uname;
                        console.log(uname.charAt(0));
                        // this.setState({username: uname});
                        // this.setState({firstLetter: uname.charAt(0)});

                        // var name = document.getElementById("showname");
                        // name.innerHTML = obj.user_name;
                        // this.setState({name: obj.user_name});
                        //
                        // var email = document.getElementById("showemail");
                        // email.innerHTML = obj.user_email;
                        // this.setState({email: obj.user_email});
                        //
                        // var role = document.getElementById("showrole");
                        // role.innerHTML = obj.user_role;
                        // this.setState({role: obj.user_role});

                        // var img = document.getElementById("profileimg");
                        // img.innerHTML = obj.user_name.charAt(0);
                    }

                    else{
                        console.log("tempo expirado");
                        window.localStorage.removeItem('token');
                        document.getElementById("tologin").click();
                        {/*<Link to={'/login'}/>*/}
                    }
                }
            }.bind(this)
        }
        else{
            // if(token != null){
            //     window.localStorage.removeItem('token');
            //     console.log("Tempo expirado - faca login");
            //     document.getElementById("tologin").click();
            // }
            // else{
                console.log("Sem sessao iniciada");
                document.getElementById("tologin").click();
            // }

            {/*<Link to={'/login'}/>*/}
            //document.location.href = '/login';
        }
    };

    handleToggle = () => {
        this.setState({ accountOpen: !this.state.accountOpen });
    };

    handleClose = event => {
        if (this.target1.contains(event.target)) {
            return;
        }

        this.setState({ accountOpen: false });
    };

    handleChange = (event, value) => {
        this.setState({ value });
    };

    handleOpenEdit = () => {
        this.setState({ editProfile: true });
    };

    handleOpenPassword = () => {
        this.setState({ editPass: true });
    };

    handleCloseEdit = () => {
        this.setState({ editProfile: false });
    };

    handleClosePassword = () => {
        this.setState({ editPass: false });
    };

    handleEditChange = username => event => {
        this.setState({[username]: event.target.value,});
    };

    handleVerifyPass = event => {
        this.setState({oldpass: event.target.value,});
    };

    handleNewPassword = event => {
        this.setState({newpass: event.target.value,});
    };

    handleConfirmPass = event => {
        return event.target.value == this.state.newpass;
    };

    // componentDidUpdate () {
    //     console.log("update");
    //     if(this.state.s){
    //         this.setState({s: false});
    //         this.setState({email: 'ana'});
    //     }
    //     else{
    //         this.setState({s: true});
    //         this.setState({email: 'outro'});
    //     }
    //
    // }

    componentDidMount () {
        console.log("componentdidmount");
        this.setState({s: !this.state.s});
        var token = window.localStorage.getItem('token');

        if(token != null){
            console.log("cdm2");
            var uname = JSON.parse(token).username;
            this.setState({username: uname});
            this.setState({firstLetter: uname.charAt(0)});

            console.log("cdm3");

            console.log("informations1");
            var obj;
            var token = window.localStorage.getItem('token');
            var d = new Date();
            var t = d.getTime();
            // var expirationData = JSON.parse(token).expirationData;
            // console.log(token);
            // console.log(t);
            // console.log(expirationData);

            if(token != null){
                var uname = JSON.parse(token).username;
                var tokenObj = JSON.parse(token);

                var user = {
                    "username": uname,
                    "token": tokenObj
                }

                var xmlHttp = new XMLHttpRequest();
                xmlHttp.open( "POST", "https://custom-tine-204615.appspot.com/rest/profile/", true);
                xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                var myJSON = JSON.stringify(user);
                xmlHttp.send(myJSON);

                xmlHttp.onreadystatechange = function() {
                    if (xmlHttp.readyState === XMLHttpRequest.DONE){
                        if(xmlHttp.status === 200){
                            console.log("informations2");
                            var response = xmlHttp.response;
                            console.log("XML response: " + response);
                            obj = JSON.parse(response);
                            console.log(obj);

                            console.log(uname.charAt(0));

                            // resolve(obj);
                        }

                        else{
                            console.log("tempo expirado");
                            window.localStorage.removeItem('token');
                            // document.getElementById("tologin").click();
                        }
                    }
                }.bind(this)
            }
            else {
                console.log("Sem sessao iniciada");
                // document.getElementById("tologin").click();
            }


                // informations.then((obj) => {
                console.log("cdm4");
                this.setState({email: obj.user_email});
                this.setState({name: obj.user_name});
                this.setState({role: obj.user_role});
                console.log(this.state.name + " " + this.state.email + " " + this.state.role);
                console.log(obj);
            // });
            xmlRequest.then((value) =>{
                console.log("cdm5");
                if(value != undefined && value.length != 0){
                    this.setState({reports: value});
                    // if(this.isMounted())
                    //     this.setState({loggedIn: true});
                    console.log(this.state.reports);
                    console.log("loggedIn");
                    console.log(this.state.isLoggedIn);
                    console.log(value);
                    this.setState({hasReports: true});
                }
                else{
                    console.log("not loggedIn");
                }
                console.log("state object");
                console.log(this.state.reports);
            });

            // var url = 'https://storage.googleapis.com/custom-tine-204615.appspot.com/user/' + uname + '/photo' ;
            //
            // var image = new Image();
            //
            // image.onload = function () {
            //     this.setState({hasPhoto: true});
            //     this.setState({photo: url});
            // }.bind(this);
            //
            // image.onerror = function () {
            //     this.setState({hasPhoto: false});
            // }.bind(this);
            //
            // image.src = url;
            //
            // console.log("hasphoto:");
            // console.log(this.state.hasPhoto);
        }
        else{
            document.getElementById("tologin").click();
        }

    }

    render() {
        const { classes } = this.props;
        const { accountOpen, value, isLoggedIn, reports, name, email, role, username, firstLetter, hasReports } = this.state;

        if(!isLoggedIn){
            return <Redirect to={"/login"}>Link</Redirect>;
        }

        return (
            <div>
                {/*{isLoggedIn ? <div/> : <Link to={"/login"}>Link</Link>}*/}

                {/*<Typography variant="display1" className={classes.title}>Perfil</Typography>*/}

                <Paper className={classes.paper} style={{margin: '0 auto'}} >

                    <Button className={classes.editProfile} onClick={this.handleOpenEdit}> <ProfileIcon /> Editar Perfil </Button>

                    <Dialog
                        open={this.state.editProfile}
                        onClose={this.handleCloseEdit}
                        aria-labelledby="simple-dialog-title"
                        aria-describedby="simple-dialog-description"
                        className={classes.dialogProfile}
                    >
                        <DialogTitle id="simple-dialog-title">{"Editar Perfil"}</DialogTitle>

                        <DialogContent>
                            {/*<img src={require('./img/user.png')} alt="Avatar" className={classes.media} width="100"/>*/}
                            <div className={classes.row}>
                                <Avatar className={classes.bigAvatar}>{firstLetter}</Avatar>
                            </div>
                            <div className="imgcontainer">
                                <input
                                    accept="image/*"
                                    className={classes.input}
                                    id="raised-button-file"
                                    multiple
                                    type="file"
                                />
                                <label htmlFor="raised-button-file"><Button component="span"><PhotoIcon/>Atualizar Foto</Button></label>
                            </div>

                            <TextField id="editusername" label="Username" className={classes.textField} value={this.state.username}
                                       onChange={this.handleEditChange('username')}/><br/>
                            <TextField id="editemail" label="Email" className={classes.textField} value={this.state.email}
                                       onChange={this.handleEditChange('email')}/><br/>
                            <TextField id="editname" label="Nome" className={classes.textField} value={this.state.name}
                                       onChange={this.handleEditChange('name')}/><br/>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleCloseEdit} color="primary">
                                Cancelar
                            </Button>
                            <Button onClick={this.handleCloseEdit} color="primary" autoFocus>
                                Guardar alteracoes
                            </Button>
                        </DialogActions>
                    </Dialog>

                    <div className={classes.settings}>
                        <Manager>
                            <Target>
                                <div
                                    ref={node => {
                                        this.target1 = node;
                                    }}
                                >
                                    <IconButton
                                        aria-owns={accountOpen ? 'menu-list-grow' : null}
                                        aria-haspopup="true"
                                        className={classes.settings}
                                        onClick={this.handleToggle}>
                                        <SettingsIcon/>
                                    </IconButton>
                                </div>
                            </Target>
                            <Popper
                                placement="bottom-start"
                                eventsEnabled={accountOpen}
                                className={classNames({ [classes.popperClose]: !accountOpen })}
                            >
                                <ClickAwayListener onClickAway={this.handleClose}>
                                    <Grow in={accountOpen} id="menu-list-grow" style={{ transformOrigin: '0 0 0' }}>
                                        <Paper>
                                            <MenuList role="menu">
                                                <MenuItem onClick={this.handleOpenPassword}>Mudar password</MenuItem>
                                                <MenuItem onClick={this.handleLogout}>Terminar Sessao</MenuItem>
                                            </MenuList>
                                        </Paper>
                                    </Grow>
                                </ClickAwayListener>
                            </Popper>
                        </Manager>
                    </div>

                    <Dialog
                        open={this.state.editPass}
                        onClose={this.handleClosePassword}
                        aria-labelledby="alert-dialog-title"
                        aria-describedby="alert-dialog-description"
                    >
                        <DialogTitle id="alert-dialog-title">{"Mudar Password"}</DialogTitle>
                        <DialogContent>
                            <TextField id="verifypass" label="Password atual" type="password" className={classes.textField}
                                       onChange={this.handleVerifyPass}/><br/>
                            <TextField id="newpass" label="Nova password" type="password" className={classes.textField}
                                       onChange={this.handleNewPassword}/><br/>
                            <TextField id="confirmpass" label="Confirmar nova password" type="password" className={classes.textField}
                                       onChange={this.handleConfirmPass}/><br/>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleClosePassword} color="primary">
                                Cancelar
                            </Button>
                            <Button onClick={this.handleClosePassword} color="primary" autoFocus>
                                Guardar alteracoes
                            </Button>
                        </DialogActions>
                    </Dialog>

                    <div className={"w3-row-padding"}>
                    <div className={"w3-third"} style={{margin: '50'}}>
                        {/*<img src={require('./img/user.png')} alt="Avatar" className={classes.media} width="200"/>*/}
                        {this.state.hasPhoto ?
                            <img src={this.state.photo} alt='Foto'
                            width={"200"}/> :

                            <Avatar className={classes.biggerAvatar} style={{margin: '0 auto'}}>
                                {this.state.firstLetter}
                            </Avatar>
                        }

                    </div>

                    <div className={"w3-twothird"}>
                        <div id="showusername" className={classes.username}>{username}</div>
                        <Typography id="showemail" component="p">{email}</Typography>
                        <div className={classes.basicInfo}>
                            {hasReports ? <b id="reports">{reports.length}</b> : <b id="reports">0</b>} Reportes de Problemas
                        </div>
                        <div className={classes.basicInfo}>
                            <b id="supports">0</b> A apoiar
                        </div>
                        <div className={classes.basicInfo}>
                            <b id="supports">0</b> Comentarios
                        </div>

                        <Typography id="showname" component="p">{name}</Typography>
                        <p id="showrole" className={classes.role}>{role}</p>

                    </div>
                    </div>

                </Paper><br/>

                <Paper className={classes.paperTwo} style={{margin: '0 auto'}}>
                    <Tabs
                        value={this.state.value}
                        onChange={this.handleChange}
                        fullWidth
                        indicatorColor="primary"
                        textColor="primary"
                    >
                        <Tab icon={<ReportsIcon />} label="REPORTES" />
                        <Tab icon={<FavoriteIcon />} label="A APOIAR" />
                        <Tab icon={<PersonPinIcon />} label="AMIGOS" />
                    </Tabs>

                    {value === 0 && hasReports &&
                        <TabContainer>
                        {reports.map(n => {
                            return(
                                <div style={{marginBottom: '50px'}}>
                                    <h2>{n.user_occurrence_title}</h2>
                                    <p>Tipo: {n.user_occurrence_type}</p>
                                    <p>Grau: {n.user_occurrence_level}</p>
                                    <p>Data do registo: {n.user_occurrence_data}</p>
                                    <img key={n.user_occurrence_data} className={classes.img} src={img2} style={{margin: '0 auto'}} alt={n.user_occurrence_title} />
                                </div>


                            )
                        })}
                    </TabContainer>}
                    {value === 0 && !hasReports && <TabContainer>Sem registos de momento</TabContainer>}
                    {value === 1 && <TabContainer>Sem apoios de momento</TabContainer>}
                    {value === 2 && <TabContainer>Sem amigos de momento</TabContainer>}
                </Paper>

                <Button id={"tologin"} component={Link} to='/login' className={classes.input} color={"primary"}>
                    Sem sessao iniciada
                </Button>
            </div>

        );
    }
}

Profile.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Profile);