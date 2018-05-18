import React, { Component } from 'react';
import NewsIcon from '@material-ui/icons/Public';
import ProfileIcon from '@material-ui/icons/PermIdentity';
import RegistPIcon from '@material-ui/icons/NoteAdd';
import OperationsIcon from '@material-ui/icons/ViewList';
import MapIcon from '@material-ui/icons/Place';
import StatsIcon from '@material-ui/icons/Poll';
import Button from 'material-ui/Button';
import Badge from 'material-ui/Badge';
import {withStyles} from "material-ui/styles/index";
import {Link} from 'react-router-dom';

const styles = theme => ({
    logo:{
      marginBottom: 60,
    },
    icons: {
        fontSize: 90,
        color: 'grey',
    },
    iconSection: {
        display: 'inline-block',
        marginLeft: 40,
        marginRight: 40,
    },
    margin: {
        margin: theme.spacing.unit * 2,
    },
});

class HomePage extends Component {
    render() {
        const {classes} = this.props;

        return (
            <div className="imgcontainer">
                <img src={require('./img/agniNoBack.png')} alt="Avatar" className={classes.logo} width={'700'}/><br/>

                <section className={classes.iconSection}>

                    <NewsIcon className={classes.icons}/><br/>
                    <Button component={Link} to={'/noticias'}>
                        Noticias
                    </Button>
                </section>

                <section className={classes.iconSection}>
                    <RegistPIcon className={classes.icons}/><br/>
                    <Button component={Link} to={'/registarproblema'}>
                        Registar Problema
                    </Button>
                </section>

                <section className={classes.iconSection}>
                    <Badge color={"primary"} badgeContent={1}>
                        <OperationsIcon className={classes.icons}/>
                    </Badge><br/>
                    <Button component={Link} to={'/testarope'}>
                        Tabela de Operacoes
                    </Button>
                </section>

                <section className={classes.iconSection}>
                    <MapIcon className={classes.icons}/><br/>
                    <Button component={Link} to={'/mapa'}>
                        Mapa
                    </Button>
                </section>

                <section className={classes.iconSection}>
                    <StatsIcon className={classes.icons}/><br/>
                    <Button component={Link} to={'/estatisticas'}>
                        Estatisticas
                    </Button>
                </section>

            </div>
        );
    }
}

export default withStyles(styles)(HomePage);