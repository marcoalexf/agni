import React, { Component } from 'react';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import operationsData from './operationsData';
import Typography from 'material-ui/Typography';
import InfoIcon from '@material-ui/icons/EventNote';
import IconButton from 'material-ui/IconButton';
import PropTypes from 'prop-types';
import {withStyles} from "material-ui/styles/index";
import {Link} from "react-router-dom";

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

class Operations extends React.Component {
    render() {
        const { classes } = this.props;

        return (
            <div>
            <Typography variant="display1" className={classes.title}>Operacoes</Typography>

            <Table className={classes.table}>
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
                    {operationsData.map(n => {
                        return (
                            <TableRow key={n.name}>
                                <TableCell> <IconButton component={Link}
                                                        to="/operacao"> <InfoIcon/> </IconButton></TableCell>
                                <TableCell>{n.name}</TableCell>
                                <TableCell>{n.type}</TableCell>
                                <TableCell>{n.date}</TableCell>
                                <TableCell>{n.state}</TableCell>
                                <TableCell numeric> {n.level}</TableCell>
                                <TableCell>{n.visibility}</TableCell>
                            </TableRow>
                        );
                    })}
                </TableBody>
            </Table>
            </div>
        );
    }
}

Operations.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles) (Operations);