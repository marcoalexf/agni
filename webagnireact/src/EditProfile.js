import React from 'react';
import PropTypes from 'prop-types';
import Typography from 'material-ui/Typography';
import TextField from 'material-ui/TextField';
import { withStyles } from 'material-ui/styles';
import { Manager, Target, Popper } from 'react-popper';
import Paper from 'material-ui/Paper';
import Button from 'material-ui/Button';
import IconButton from 'material-ui/IconButton';
import ProfileIcon from '@material-ui/icons/Create';
import SettingsIcon from '@material-ui/icons/Settings';
import ClickAwayListener from 'material-ui/utils/ClickAwayListener';
import Grow from 'material-ui/transitions/Grow';
import { MenuItem, MenuList } from 'material-ui/Menu';
import classNames from 'classnames';
import WallpaperIcon from '@material-ui/icons/Wallpaper';
import FavoriteIcon from '@material-ui/icons/Favorite';
import ReportsIcon from '@material-ui/icons/ViewList';
import PersonPinIcon from '@material-ui/icons/PersonPin';
import Tabs, { Tab } from 'material-ui/Tabs';
import {Link} from 'react-router-dom';
import Avatar from 'material-ui/Avatar';

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
    picture : {
        fontSize: 90,
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
    textFieldUsername: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        width: 200,
        fontFamily: 'Montserrat',
        fontSize: 20,
    },
    bootstrapRoot: {
        // padding: 0,
        // 'label + &': {
        //     marginTop: theme.spacing.unit * 3,
        // },
    },
    bootstrapInputUsername: {
        borderRadius: 4,
        // backgroundColor: theme.palette.common.white,
        border: '1px solid #ced4da',
        // padding: '10px 12px',
        width: 400,
        // transition: theme.transitions.create(['border-color', 'box-shadow']),
        fontFamily: 'Montserrat',
        fontSize: 48,
        // '&:focus': {
        //     borderColor: '#80bdff',
        //     boxShadow: '0 0 0 0.2rem rgba(0,123,255,.25)',
        // },
    },
    bootstrapInputEmail: {
        borderRadius: 4,
        // backgroundColor: theme.palette.common.white,
        border: '1px solid #ced4da',
        // padding: '10px 12px',
        width: 400,
        // transition: theme.transitions.create(['border-color', 'box-shadow']),
        // '&:focus': {
        //     borderColor: '#80bdff',
        //     boxShadow: '0 0 0 0.2rem rgba(0,123,255,.25)',
        // },
    },
    bootstrapFormLabel: {
        fontSize: 18,
    },
});

// const username = xmlRequest.then(
//     <EditProfile username={'mar'}></EditProfile>
// )

let xmlRequest = new Promise(function(resolve, reject) {
    var obj;
    var token = window.localStorage.getItem('token');

    if (token != null) {
        var uname = JSON.parse(token).username;
        var tokenObj = JSON.parse(token);

        // if(tokenObj.expirationData){
        //  tratar para o caso do token ter expirado
        // }

        var user = {
            "username": uname,
            "token": tokenObj
        }

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("POST", "http://localhost:8080/rest/profile/", true);
        xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        var myJSON = JSON.stringify(user);
        xmlHttp.send(myJSON);

        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState === XMLHttpRequest.DONE) {
                if (xmlHttp.status === 200) {
                    var response = xmlHttp.response;
                    console.log("XML response: " + response);
                    obj = JSON.parse(response);

                    // var username = document.getElementById("showusername");
                    // username.innerHTML = uname;
                    resolve(obj.user_name);

                    var name = document.getElementById("showname");
                    name.innerHTML = obj.user_name;

                    var email = document.getElementById("showemail");
                    email.innerHTML = obj.user_email;

                    var role = document.getElementById("showrole");
                    role.innerHTML = obj.user_role;

                    // var img = document.getElementById("profileimg");
                    // img.innerHTML = obj.user_name.charAt(0);
                }

                else {
                    //TO DO- ver se o tempo ja expirou antes de "chatear" o server
                    console.log("tempo expirado");
                    window.localStorage.removeItem('token');
                    document.location.href = '/login';
                }
            }
        }
    }
    else {
        document.location.href = '/login';
    }
})




class EditProfile extends React.Component {
    constructor(){
        super();

        this.state = {
            accountOpen: false,
            value: 0,
        };

        xmlRequest.then((value) => {
            console.log("value");
            console.log(value);
            this.setState({username: value});
            console.log(this.state.username);
            // this.setState({email: value.user_email});
            // this.setState({name: value.user_name});
            // this.setState({role: value.user_role});
        })
    }


    loadInformations = () =>{
        var obj;
        var token = window.localStorage.getItem('token');

        if(token != null){
            var uname = JSON.parse(token).username;
            var tokenObj = JSON.parse(token);

            // if(tokenObj.expirationData){
            //  tratar para o caso do token ter expirado
            // }

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

                        // var username = document.getElementById("showusername");
                        // username.innerHTML = uname;

                        var name = document.getElementById("showname");
                        name.innerHTML = obj.user_name;

                        var email = document.getElementById("showemail");
                        email.innerHTML = obj.user_email;

                        var role = document.getElementById("showrole");
                        role.innerHTML = obj.user_role;

                        // var img = document.getElementById("profileimg");
                        // img.innerHTML = obj.user_name.charAt(0);
                    }

                    else{
                        //TO DO- ver se o tempo ja expirou antes de "chatear" o server
                        console.log("tempo expirado");
                        window.localStorage.removeItem('token');
                        document.location.href = '/login';
                    }
                }
            }
        }
        else{
            document.location.href = '/login';
        }
    }

    handleUsernameChange = username => event => {
        this.setState({[username]: event.target.value,});
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

    render() {
        const { classes } = this.props;
        const { accountOpen, value } = this.state;

        return (
            <div onLoad={this.loadInformations}>
                <Typography variant="display1" className={classes.title}>Perfil</Typography>

                <Paper className={classes.paper} style={{margin: '0 auto'}} >

                    <Button disabled className={classes.editProfile}> <ProfileIcon /> Editar Perfil </Button>

                    <div className={classes.settings}>
                        <Manager>
                            <Target>
                                <div
                                    ref={node => {
                                        this.target1 = node;
                                    }}
                                >
                                    <IconButton
                                        disabled
                                        aria-owns={accountOpen ? 'menu-list-grow' : null}
                                        aria-haspopup="true"
                                        className={classes.settings}
                                        onClick={this.handleToggle}>
                                        <SettingsIcon/>
                                    </IconButton >
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
                                                <MenuItem onClick={this.handleProfileOption}>Mudar password</MenuItem>
                                                <MenuItem onClick={this.handleLogout}>Terminar Sessao</MenuItem>
                                            </MenuList>
                                        </Paper>
                                    </Grow>
                                </ClickAwayListener>
                            </Popper>
                        </Manager>
                    </div>

                    <img src={require('./img/user.png')} alt="Avatar" className={classes.media} width="200"/>
                    {/*<div><Avatar id={"profileimg"} className={classes.avatar}></Avatar></div>*/}
                    {/*<IconButton><WallpaperIcon className={classes.picture}/></IconButton>*/}

                    <div className={classes.informations}>
                        {/*<TextField id="username" label="Username" className={classes.textFieldUsername} value={this.state.username}*/}
                                   {/*onChange={this.handleUsernameChange('username')}/>*/}

                        <TextField
                            defaultValue={this.state.username}
                            label="Username"
                            id="username"
                            InputProps={{
                                disableUnderline: true,
                                classes: {
                                    root: classes.bootstrapRoot,
                                    input: classes.bootstrapInputUsername,
                                },
                            }}
                            InputLabelProps={{
                                shrink: true,
                                className: classes.bootstrapFormLabel,
                            }}
                        />

                        <TextField
                            defaultValue={this.state.email}
                            label="Email"
                            id="email"
                            InputProps={{
                                disableUnderline: true,
                                classes: {
                                    root: classes.bootstrapRoot,
                                    input: classes.bootstrapInputEmail,
                                },
                            }}
                            InputLabelProps={{
                                shrink: true,
                                className: classes.bootstrapFormLabel,
                            }}
                        />


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

                </Paper>
            </div>

        );
    }
}

EditProfile.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(EditProfile);