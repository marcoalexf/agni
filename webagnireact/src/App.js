import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import classNames from 'classnames';
import Drawer from 'material-ui/Drawer';
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import { Manager, Target, Popper } from 'react-popper';
import Button from 'material-ui/Button';
import List from 'material-ui/List';
import Divider from 'material-ui/Divider';
import IconButton from 'material-ui/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import AccountIcon from '@material-ui/icons/AccountCircle';
import LogoutIcon from '@material-ui/icons/ExitToApp';
import { mainMenuListItems, logoutItem } from './sideBarData';
import {Link, Route, Switch} from 'react-router-dom';
import Register from './Register';
import Login from './Login';
import Home from './Home';
import HomePage from './HomePage';
import News from './News';
import NewsTwo from './NewsTwo';
import Profile from './Profile';
import RegistProblem from './NewRegistProblem';
import ThankYou from './ThankYou';
import Operations from './Operations';
import TestOperations from './TestOperations';
import Map from './Map';
import MapTwo from './MapFive';
import Statistics from './Statistics';
import Operation from './OperationsInformations';
import AboutUs from './AboutUs';
import ClickAwayListener from 'material-ui/utils/ClickAwayListener';
import Grow from 'material-ui/transitions/Grow';
import Paper from 'material-ui/Paper';
import { MenuItem, MenuList } from 'material-ui/Menu';
import { createMuiTheme } from 'material-ui/styles';
import Avatar from 'material-ui/Avatar';
import Typography from 'material-ui/Typography';

const drawerWidth = 240;

const theme = createMuiTheme({
    palette: {
        primary: {
            light: '#757ce8',
            main: '#3f50b5',
            dark: '#002884',
            contrastText: '#fff',
        },
        secondary: {
            light: '#ff7961',
            main: '#f44336',
            dark: '#ba000d',
            contrastText: '#000',
        },
    },
});

const styles = theme => ({
    root: {
        flexGrow: 1,
        //height: 430,
        zIndex: 1,
        overflow: 'hidden',
        position: 'relative',
        display: 'flex',
    },
    appBar: {
        zIndex: theme.zIndex.drawer + 1,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
    },
    appBarShift: {
        marginLeft: drawerWidth,
        width: `calc(100% - ${drawerWidth}px)`,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    menuButton: {
        marginLeft: 12,
        marginRight: 36,
    },
    loginButton:{
        flex:1,
        fontFamily: 'Roboto Mono',
        fontSize: 12,
    },
    hide: {
        display: 'none',
    },
    drawerPaper: {
        position: 'relative',
        whiteSpace: 'nowrap',
        width: drawerWidth,
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    drawerPaperClose: {
        overflowX: 'hidden',
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
        width: theme.spacing.unit * 7,
        [theme.breakpoints.up('sm')]: {
            width: theme.spacing.unit * 9,
        },
    },
    toolbar: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        padding: '0 8px',
        ...theme.mixins.toolbar,
    },
    content: {
        flexGrow: 1,
        backgroundColor: theme.palette.background.default,
        padding: theme.spacing.unit * 3,
    },
    account: {
        width: 400,
    },
    popperClose: {
        pointerEvents: 'none',
    },
    input: {
        display: 'none',
    },
    row: {
        display: 'flex',
        justifyContent: 'center',
    },
    avatars:{
        margin: 10,
        width: 60,
        height: 60,
    },
    username:{
        margin: 30,
        fontFamily: 'Montserrat',
    },
    logout:{
        marginLeft: 20,
    }
});

class MiniDrawer extends React.Component {
    constructor(){
        super();
        this.handleLogout = this.handleLogout.bind(this);
    }
    state = {
        open: true,
        accountOpen: false,
        changeRouter: false,
    };

    handleDrawerOpen = () => {
        this.setState({ open: true });
    };

    handleDrawerClose = () => {
        this.setState({ open: false });
    };

    handleOpenAccount = event => {
        this.setState({ anchorEl: event.currentTarget });
    };

    handleToggle = () => {
        this.setState({ accountOpen: !this.state.accountOpen });
    };

    handleClose = event => {
        if (this.target1.contains(event.target)) {
            return;
        }
        console.log("handleClose");
        this.setState({ accountOpen: false });
    };

    handleLogout = event => {
        var token = window.localStorage.getItem('token');

        if(token != null){
            var uname = JSON.parse(token).username;
            var tokenID = JSON.parse(token);

            var data = {
                "username": uname,
                "userID": tokenID.userID,
                "tokenID": tokenID.tokenID,
                "creationData": tokenID.creationData,
                "expirationData": tokenID.expirationData
            }

            console.log("token:");
            console.log(token);

            if(uname!= null){
                var xmlHttp = new XMLHttpRequest();
                xmlHttp.open( "POST", "https://custom-tine-204615.appspot.com/rest/logout");
                xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                var myJSON = JSON.stringify(data);
                xmlHttp.send(myJSON);

                xmlHttp.onreadystatechange = function() {
                    if(xmlHttp.readyState == XMLHttpRequest.DONE) {
                        if (xmlHttp.status == 200) {
                            var response = xmlHttp.responseText;
                            console.log("XML response: " + response);
                            //window.localStorage.removeItem('token');
                            console.log("sucesso");

                            // if (this.target1.contains(event.target)) {
                            //     return;
                            // }
                            console.log("handleClose2");

                            this.setState({ accountOpen: false });

                            document.getElementById("tologin").click();
                            // document.location.href = '/login';
                            // window.localStorage.removeItem('token');
                        }

                        else {
                            console.log("tempo expirado");
                        }
                    }
                     window.localStorage.removeItem('token');
                }.bind(this)
            }
        }

        else{
            console.log("nao existe nenhuma conta com login de momento");
        }
    }

    isLogin = () => {
        var token = window.localStorage.getItem('token');
        var d = new Date();
        var t = d.getTime();

        if(token == null){
            return false;
        }
        else{
            var expirationData = JSON.parse(token).expirationData;
            console.log("tempo atual: " + t);
            console.log("data de expiracao: " + expirationData);

            if(expirationData <= t){
                console.log("tempo expirado");
                window.localStorage.removeItem('token');
                return false;
            }

            else{
                return true;
            }
        }

    };

    username = () => {
        var token = window.localStorage.getItem('token');

        if(token != null){
            var uname = JSON.parse(token).username;
            // this.setState({username: uname});
            // this.setState({firstLetter: uname.charAt(0)});
            return uname;
        }
        else{
            return '';
        }
    };

    render() {
        const { classes, theme } = this.props;
        const { accountOpen } = this.state;

        return (
            <div className={classes.root}>
                <AppBar
                    position="absolute" color="default"
                    className={classNames(classes.appBar, this.state.open && classes.appBarShift)}
                >
                    <Toolbar disableGutters={!this.state.open}>
                        <IconButton
                            color="inherit"
                            aria-label="open drawer"
                            onClick={this.handleDrawerOpen}
                            className={classNames(classes.menuButton, this.state.open && classes.hide)}
                        >
                            <MenuIcon />
                        </IconButton>
                        <q className={classNames(classes.loginButton)}>
                            Menos florestas negras, mais caminhos verdes e céus mais azuis
                        </q>

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
                                        color="inherit"
                                        onClick={this.handleToggle}
                                        className={this.isLogin() ? null: classes.input}>
                                        <AccountIcon/>
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
                                            <div className={classes.row}>
                                                <Avatar className={classes.avatars}>{this.username().charAt(0)}</Avatar>
                                                <b className={classes.username}>{this.username()}</b><br/>
                                            </div>
                                            <div>
                                                <Button component={Link} to={'/perfil'}>Perfil</Button>
                                                <Button className={classes.logout} onClick={this.handleLogout}>Terminar Sessão</Button>
                                            </div>
                                            {/*<MenuList role="menu">*/}
                                                {/*<MenuItem component={Link} to={'/perfil'}>Perfil</MenuItem>*/}
                                                {/*<MenuItem onClick={this.handleLogout}>*/}
                                                    {/*<LogoutIcon/> Terminar Sessao*/}
                                                {/*</MenuItem>*/}
                                            {/*</MenuList>*/}
                                        </Paper>
                                    </Grow>
                                </ClickAwayListener>
                            </Popper>
                        </Manager>

                        <Button component={Link} to="/login" color="inherit" className={this.isLogin() ? classes.input : null}>
                            Entrar
                        </Button>
                        <Button component={Link} to="/register" color="inherit" className={this.isLogin() ? classes.input : null}>
                            Criar Conta
                        </Button>
                    </Toolbar>
                </AppBar>
                <Drawer
                    variant="permanent"
                    classes={{
                        paper: classNames(classes.drawerPaper, !this.state.open && classes.drawerPaperClose),
                    }}
                    open={this.state.open}
                >
                    <div className={classes.toolbar}>
                        <IconButton onClick={this.handleDrawerClose}>
                            {theme.direction === 'rtl' ? <ChevronRightIcon /> : <ChevronLeftIcon />}
                        </IconButton>
                    </div>

                    <Divider />
                    <List>{mainMenuListItems}</List>
                    <Divider />
                    <List>{logoutItem}</List>

                    <Button id={"tologin"} component={Link} to='/login' className={classes.input} color={"primary"}>
                        Sem sessao iniciada
                    </Button>
                </Drawer>
                <main className={classes.content}>
                    <div className={classes.toolbar} />
                    <Switch>
                        <Route exact path='/' component={HomePage}/>
                        <Route path='/login' component={Login}/>
                        <Route path='/register' component={Register}/>
                        <Route path='/noticias' component={NewsTwo}/>
                        <Route path='/perfil' component={Profile}/>
                        <Route path='/registarproblema' component={RegistProblem}/>
                        <Route path='/operacoes' component={Operations}/>
                        <Route path='/mapa' component={MapTwo}/>
                        <Route path='/estatisticas' component={Statistics}/>
                        <Route path='/operacao' component={Operation}/>
                        <Route path='/obrigada' component={ThankYou}/>
                        <Route path='/sobrenos' component={AboutUs}/>
                        <Route path='/testarope' component={TestOperations}/>
                    </Switch>
                </main>
            </div>
        );
    }
}

MiniDrawer.propTypes = {
    classes: PropTypes.object.isRequired,
    theme: PropTypes.object.isRequired,
};

export default withStyles(styles, { withTheme: true })(MiniDrawer);