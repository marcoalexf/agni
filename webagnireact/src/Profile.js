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
import {Link} from 'react-router-dom';
import Dialog, {DialogActions, DialogContent, DialogContentText, DialogTitle,} from 'material-ui/Dialog';
import TextField from 'material-ui/TextField';
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
    }),
    editProfile:{
        marginLeft: 550,
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
    input: {
        display: 'none',
    },
});

class Profile extends React.Component {
    constructor(props){
        super(props);

        this.state = {
            accountOpen: false,
            value: 0,
            editProfile: false,
            editPass: false,
        };

        this.loadInformations = this.loadInformations.bind(this);
    }

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
            xmlHttp.open( "POST", "http://localhost:8080/rest/profile/", true);
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
                        this.setState({username: uname});
                        this.setState({firstLetter: uname.charAt(0)});

                        var name = document.getElementById("showname");
                        name.innerHTML = obj.user_name;
                        this.setState({name: obj.user_name});

                        var email = document.getElementById("showemail");
                        email.innerHTML = obj.user_email;
                        this.setState({email: obj.user_email});

                        var role = document.getElementById("showrole");
                        role.innerHTML = obj.user_role;
                        this.setState({role: obj.user_role});

                        // var img = document.getElementById("profileimg");
                        // img.innerHTML = obj.user_name.charAt(0);
                    }

                    else{
                        console.log("tempo expirado");
                        window.localStorage.removeItem('token');
                        {/*<Link to={'/login'}/>*/}
                    }
                }
            }.bind(this)
        }
        else{
            if(token != null){
                window.localStorage.removeItem('token');
                console.log("Tempo expirado - faca login");
            }
            else
                console.log("Sem sessao iniciada");
            {/*<Link to={'/login'}/>*/}
            //document.location.href = '/login';
        }
    }

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

    render() {
        const { classes } = this.props;
        const { accountOpen, value } = this.state;

        return (
            <div onLoad={this.loadInformations}>
                <Typography variant="display1" className={classes.title}>Perfil</Typography>

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
                                <Avatar className={classes.bigAvatar}>{this.state.firstLetter}</Avatar>
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

                    <img src={require('./img/user.png')} alt="Avatar" className={classes.media} width="200"/>
                    {/*<div><Avatar id={"profileimg"} className={classes.avatar}></Avatar></div>*/}

                    <div className={classes.informations}>
                        <div id="showusername" className={classes.username}></div>
                        <Typography id="showemail" component="p"></Typography>
                        <div className={classes.basicInfo}>
                            <b id="reports">0</b> Reportes de Problemas
                        </div>
                        <div className={classes.basicInfo}>
                            <b id="supports">0</b> A apoiar
                        </div>
                        <div className={classes.basicInfo}>
                            <b id="supports">0</b> Comentarios
                        </div>

                        <Typography id="showname" component="p"></Typography>
                        <p id="showrole" className={classes.role}></p>

                    </div>

                </Paper><br/>

                <Paper className={classes.paper} style={{margin: '0 auto'}}>
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

                    {value === 0 && <TabContainer>Sem reportes de momento</TabContainer>}
                    {value === 1 && <TabContainer>Sem apoios de momento</TabContainer>}
                    {value === 2 && <TabContainer>Sem amigos de momento</TabContainer>}
                </Paper>
            </div>

        );
    }
}

Profile.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Profile);