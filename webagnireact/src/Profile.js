import React from 'react';
import PropTypes from 'prop-types';
import Typography from 'material-ui/Typography';
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
import FavoriteIcon from '@material-ui/icons/Favorite';
import ReportsIcon from '@material-ui/icons/ViewList';
import PersonPinIcon from '@material-ui/icons/PersonPin';
import Tabs, { Tab } from 'material-ui/Tabs';

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
});

class Profile extends React.Component {
    state = {
        accountOpen: false,
        value: 0,
    };

    loadInformations = () =>{
        var obj;
        var token = window.localStorage.getItem('token');

        if(token != null){
            var uname = JSON.parse(token).username;
            var tokenID = JSON.parse(token);

            // if(tokenObj.expirationData){
            //  tratar para o caso do token ter expirado
            // }

            var user = {
                "username": uname,
                "token": tokenID
            }

            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "POST", "http://localhost:8080/rest/profile/", true);
            xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            var myJSON = JSON.stringify(user);
            xmlHttp.send(myJSON);

            xmlHttp.onreadystatechange = function() {
                if (xmlHttp.readyState == XMLHttpRequest.DONE){
                    if(xmlHttp.status == 200){
                        var response = xmlHttp.response;
                        console.log("XML response: " + response);
                        obj = JSON.parse(response);

                        var username = document.getElementById("showusername");
                        username.innerHTML = uname;

                        var name = document.getElementById("showname");
                        name.innerHTML = obj.user_name;

                        var email = document.getElementById("showemail");
                        email.innerHTML = obj.user_email;

                        var role = document.getElementById("showrole");
                        role.innerHTML = obj.user_role;
                    }

                    else{
                        //TO DO- ver se o tempo ja expirou antes de "chatear" o server
                        console.log("tempo expirado");
                        window.localStorage.removeItem('token');
                    }
                }
            }
        }
        else{
            document.location.href = '/login';
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

    render() {
        const { classes } = this.props;
        const { accountOpen, value } = this.state;

        return (
            <div onLoad={this.loadInformations}>
                <Typography variant="display1" className={classes.title}>Perfil</Typography>

                <Paper className={classes.paper} style={{margin: '0 auto'}} >

                    <Button className={classes.editProfile}> <ProfileIcon /> Editar Perfil </Button>

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

                    <div className={classes.informations}>
                        <div id="showusername" className={classes.username}></div>
                        <Typography id="showemail" component="p"></Typography>
                        <div className={classes.basicInfo}>
                            <b id="reports">0</b> <text>Reportes de Problemas</text>
                        </div>
                        <div className={classes.basicInfo}>
                            <b id="supports">0</b> <text>A apoiar</text>
                        </div>
                        <div className={classes.basicInfo}>
                            <b id="supports">0</b> <text>Comentarios</text>
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