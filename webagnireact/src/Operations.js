import React, { Component } from 'react';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import operationsData from './operationsData';
import Typography from 'material-ui/Typography';
import InfoIcon from '@material-ui/icons/EventNote';
import IconButton from 'material-ui/IconButton';
import PropTypes from 'prop-types';
import {withStyles} from "material-ui/styles/index";
import {Link} from "react-router-dom";
import Button from 'material-ui/Button';

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
});

function makeAJAXCall(){
    console.log("comeco da funcao");
    var map;
    var token = window.localStorage.getItem('token');
    var uname = JSON.parse(token).username;
    var tokenObj = JSON.parse(token);

    var user = {
        "username": uname,
        "token": tokenObj,
        "showPrivate": true //MUDAR ISTO DEPOIS
    }

    console.log("pedido");
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("POST", "http://localhost:8080/rest/occurrence/list", true);
    xmlHttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    var myJSON = JSON.stringify(user);
    xmlHttp.send(myJSON);

    console.log("esperar pelo estado");
    xmlHttp.onreadystatechange = function () {
        console.log("1");
        if (xmlHttp.readyState === 4) {
            console.log("2");
            if(xmlHttp.status === 200){
                console.log("3");
                var response = xmlHttp.response;
                var obj = JSON.parse(response);
                map = obj[0];
                console.log(map);

                var title = document.getElementById("showtitle");
                title.innerHTML = map.user_occurrence_title;

                var type = document.getElementById("showtype");
                type.innerHTML = map.user_occurrence_type;

                //callback(FillTable(map));
            }
            else {
                console.log("tempo expirado");
            }
        }
        else{
            console.log("4");
        }
    }.bind(this);
}

function FillTable(data){
    const myPromise = new Promise(makeAJAXCall);
    myPromise.then( function () {
            const object = [data];
            const table =
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Informações</TableCell>
                            <TableCell>Nome</TableCell>
                            <TableCell>Tipo</TableCell>
                            <TableCell>Data</TableCell>
                            <TableCell>Estado</TableCell>
                            <TableCell numeric>Grau de urgência</TableCell>
                            <TableCell>Visibilidade</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {object.map(occ => {
                            return (
                                <TableRow key={occ.user_occurrence_title}>
                                    <TableCell> <IconButton component={Link}
                                                            to="/operacao"> <InfoIcon/> </IconButton></TableCell>
                                    <TableCell>{occ.user_occurrence_title}</TableCell>
                                    <TableCell>{occ.user_occurrence_type}</TableCell>
                                    <TableCell>{occ.user_occurrence_data}</TableCell>
                                    <TableCell>{'nao tratado'}</TableCell>
                                    <TableCell numeric> {occ.user_occurrence_level}</TableCell>
                                    <TableCell>{occ.user_occurrence_visibility}</TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>;

            //$('.table').append(table);

            return(
                {table}
            )
        }
    );
}

class Operations extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            occurrences: 'null',
        };
    }

    render() {
        const { classes } = this.props;


        return (
            <div>
                <Typography variant="display1" className={classes.title}>Operacoes</Typography>

                <div className="table">
                    <FillTable/>
                    {/*{makeAJAXCall(FillTable)}*/}
                </div>

                <div id="showtitle" className={classes.username}></div>
                <div id="showtype"></div>

                {/*<FillTable/>*/}

                {/*<Table className={classes.table}>*/}
                    {/*<TableHead>*/}
                        {/*<TableRow>*/}
                            {/*<TableCell>Informações</TableCell>*/}
                            {/*<TableCell>Nome</TableCell>*/}
                            {/*<TableCell>Tipo</TableCell>*/}
                            {/*<TableCell>Data</TableCell>*/}
                            {/*<TableCell>Estado</TableCell>*/}
                            {/*<TableCell numeric>Grau de urgência</TableCell>*/}
                            {/*<TableCell>Visibilidade</TableCell>*/}
                        {/*</TableRow>*/}
                    {/*</TableHead>*/}

                    {/*<TableBody>*/}
                        {/*{operationsData.map(n => {*/}
                            {/*return (*/}
                                {/*<TableRow key={n.name}>*/}
                                    {/*<TableCell> <IconButton component={Link}*/}
                                                            {/*to="/operacao"> <InfoIcon/> </IconButton></TableCell>*/}
                                    {/*<TableCell>{n.name}</TableCell>*/}
                                    {/*<TableCell>{n.type}</TableCell>*/}
                                    {/*<TableCell>{n.date}</TableCell>*/}
                                    {/*<TableCell>{n.state}</TableCell>*/}
                                    {/*<TableCell numeric> {n.level}</TableCell>*/}
                                    {/*<TableCell>{n.visibility}</TableCell>*/}
                                {/*</TableRow>*/}
                            {/*);*/}
                        {/*})}*/}
                    {/*</TableBody>*/}

            {/* </Table> */}

                <Button onClick={this.loadOperations}>Pedido Rest</Button>
            </div>
        );
    }
}

Operations.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles) (Operations);