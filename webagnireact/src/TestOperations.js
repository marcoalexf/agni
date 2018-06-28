import React from 'react';
import PropTypes from 'prop-types';
import Table, { TableBody, TableCell, TablePagination, TableHead, TableRow } from 'material-ui/Table';
import Async from 'react-promise';
import operationsData from './operationsData';
import Radio, { RadioGroup } from 'material-ui/Radio';
import InfoIcon from '@material-ui/icons/Info';
import WarningIcon from '@material-ui/icons/Error';
import IconButton from 'material-ui/IconButton';
import UpdateListIcon from '@material-ui/icons/Cached';
import Toolbar from 'material-ui/Toolbar';
import Tooltip from 'material-ui/Tooltip';
import Typography from 'material-ui/Typography';
import blue from 'material-ui/colors/blue';
import grey from 'material-ui/colors/grey';
import green from 'material-ui/colors/lightGreen';
import red from 'material-ui/colors/red';
import amber from 'material-ui/colors/amber';
import yellow from 'material-ui/colors/yellow';
import {withStyles} from "material-ui/styles/index";
import {Link} from "react-router-dom";
import CircularProgress from '@material-ui/core/CircularProgress';

const CustomTableCell = withStyles(theme => ({
    head: {
        backgroundColor: blue[200],
        color: grey[50],
        //color: theme.palette.common.white,
    },
    body: {
        fontSize: 14,
    },
}))(TableCell);

const styles = theme => ({
    title:{
        margin:20,
    },
    root: {
        width: '100%',
        marginTop: theme.spacing.unit * 3,
        overflowX: 'auto',
    },
    table: {
        minWidth: 700,
    },
    spacer: {
        flex: '1 1 100%',
    },
    toolbarroot: {
        paddingRight: theme.spacing.unit,
    },
    toolbartitle: {
        flex: '0 0 auto',
    },
    severe: {
        color: red[500],
    },
    lessSerious:{
        color: green[400],
    },
    serious: {
        color: amber[400],
    },


});

// function makeAJAXCall(){
//     //return new Promise(function(resolve, reject) {
//             console.log("comeco da funcao");
//             var map;
//             var token = window.localStorage.getItem('token');
//             var uname = JSON.parse(token).username;
//             var tokenObj = JSON.parse(token);
//
//             var user = {
//                 "username": uname,
//                 "token": tokenObj,
//                 "showPrivate": true //MUDAR ISTO DEPOIS
//             }
//
//             console.log("pedido");
//             var xmlHttp = new XMLHttpRequest();
//             xmlHttp.open("POST", "https://liquid-layout-196103.appspot.com/rest/occurrence/list", true);
//             xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
//             var myJSON = JSON.stringify(user);
//             xmlHttp.send(myJSON);
//
//             console.log("esperar pelo estado");
//             xmlHttp.onreadystatechange = function () {
//                 console.log("1");
//                 if (xmlHttp.readyState === 4) {
//                     console.log("2");
//                     if (xmlHttp.status === 200) {
//                         console.log("3");
//                         var response = xmlHttp.response;
//                         var obj = JSON.parse(response);
//                         map = obj[0];
//                         console.log(map);
//                         // resolve(map);
//
//                     }
//                     else {
//                         console.log("tempo expirado");
//                         // reject(Error('Tempo expirado'));
//                     }
//                 }
//                 else {
//                     console.log("4");
//                 }
//                 //  }.bind(this);
//             }
//             // .bind(this);
//         //}.bind(this)
//     // );
//
// }

let EnhancedTableToolbar = props => {
    const { classes } = props;

    return (
        <Toolbar
            className={classes.toolbarroot}
        >
            <div className={classes.toolbartitle}>
                {(
                    <Typography variant="title" id="tableTitle">
                        Tabela de Ocorrências
                    </Typography>
                )}
            </div>
            <div className={classes.spacer} />
            <div className={classes.actions}>
                {(
                    <Tooltip title="Atualizar lista">
                        <IconButton aria-label="Atualizar lista">
                            <UpdateListIcon />
                        </IconButton>
                    </Tooltip>
                )}
            </div>
        </Toolbar>
    );
};

EnhancedTableToolbar.propTypes = {
    classes: PropTypes.object.isRequired,
};

EnhancedTableToolbar = withStyles(styles)(EnhancedTableToolbar);

let prom = new Promise(function(resolve, reject) {
    setTimeout(function() {
        resolve('a value')
    }, 100)
});

function xmlRequest(){
    return new Promise(resolve => {
        console.log("xmlRequest");
        // var t = true;
        // var token = window.localStorage.getItem('token');
        // var tokenObj = JSON.parse(token);
        //if(token != null){
        //     var uname = JSON.parse(token).username;
            var map;

            // var user = {
            //     //"username": uname,
            //     "token": tokenObj,
            //     "showPrivate": true //MUDAR ISTO DEPOIS
            // }

            console.log("pedido");
            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open("GET", "https://custom-tine-204615.appspot.com/rest/occurrence/list", true);
            //xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            //var myJSON = JSON.stringify(user);
            //xmlHttp.send(myJSON);
            xmlHttp.send();

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
                        // var array = Object.values(map);
                        // console.log(array);
                        // console.log(operationsData);
                        resolve(obj.mapList);
                        // resolve('xml value')
                    }
                    else {
                        console.log("tempo expirado");
                        // reject(Error('Tempo expirado'));
                        //TODO - Link to Login
                    }
                }
            }

            // if(t=true)
            //     resolve('xml value')
        //}

    });
}

class TestOperations extends React.Component {
    state={
        object: [
            {user_occurrence_title: ''}],
        page: 0,
        rowsPerPage: 5,
        loading: true,
    };

    async componentDidMount () {
        let r = await xmlRequest();
        this.setState({object: r});
        this.setState({loading: false});
        console.log("state object");
        console.log(this.state.object);
    }

    handleChangePage = (event, page) => {
        this.setState({ page });
    };

    handleChangeRowsPerPage = event => {
        this.setState({ rowsPerPage: event.target.value });
    };

    colorByLevel = (level) => {
        if(level==1)
            return green[700];
        else if(level==2)
            return green[500];
        else if(level==3)
            return yellow[600];
        else if(level ==4)
            return amber[700];
        else
            return red[600];
    };

    render () {
        const {classes} = this.props;
        const { object, rowsPerPage, page, loading } = this.state;

        return (
            <div>
                {loading && <CircularProgress />}
                {!loading && <div>
                <EnhancedTableToolbar></EnhancedTableToolbar>
                <Table>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>Informações</CustomTableCell>
                            <CustomTableCell>Nome</CustomTableCell>
                            <CustomTableCell>Tipo</CustomTableCell>
                            <CustomTableCell>Data</CustomTableCell>
                            <CustomTableCell>Estado</CustomTableCell>
                            <CustomTableCell numeric>Grau de urgência</CustomTableCell>
                            <CustomTableCell>Visibilidade</CustomTableCell>
                        </TableRow>
                    </TableHead>

                    <TableBody>
                        {object.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(n => {
                            return (
                                <TableRow key={n.user_occurrence_data}>
                                    <TableCell> <IconButton component={Link}
                                                            to="/operacao"> <InfoIcon/> </IconButton></TableCell>
                                    <TableCell>{n.user_occurrence_title}</TableCell>
                                    <TableCell>{n.user_occurrence_type}</TableCell>
                                    <TableCell>{n.user_occurrence_date}</TableCell>
                                    <TableCell>nao tratado</TableCell>
                                    <TableCell numeric
                                               style={{color: this.colorByLevel(n.user_occurrence_level)}}>
                                        <WarningIcon color={this.colorByLevel(n.user_occurrence_level)}/>
                                        {n.user_occurrence_level}</TableCell>
                                    <TableCell>{n.user_occurrence_visibility ? 'publico' : 'privado'}</TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
                <TablePagination
                    component="div"
                    count={object.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    backIconButtonProps={{
                        'aria-label': 'Previous Page',
                    }}
                    nextIconButtonProps={{
                        'aria-label': 'Next Page',
                    }}
                    onChangePage={this.handleChangePage}
                    onChangeRowsPerPage={this.handleChangeRowsPerPage}
                />
                </div>}
            </div>
        )
    }
}

TestOperations.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles) (TestOperations);