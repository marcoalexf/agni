import React from 'react';
import Typography from 'material-ui/Typography';
import Card, { CardActions, CardContent, CardMedia } from 'material-ui/Card';
import Button from 'material-ui/Button';
import MailIcon from '@material-ui/icons/MailOutline';
import {withStyles} from "material-ui/styles/index";

const styles =  theme => ({
    quote: {
        margin: 20,
        textAlign: 'center',
        fontFamily: 'Inconsolata',
        fontSize: 15,
    },
    title: {
        margin: 40,
        textAlign: 'center',
        fontFamily: 'Roboto Mono',
        fontSize: 30,
    },
    card: {
        width: 345,
        heigth: 400,
        display:'inline-block',
        margin: 20,
    },
    media: {
        height: 0,
        paddingTop: '56.25%', // 16:9
    },
    name:{
        fontFamily: 'Montserrat',
        fontSize: 30,
        marginBottom: 10,
    },
    role:{
        fontFamily: 'Montserrat',
        fontSize: 15,
        color: 'grey',
        marginBottom: 10,
    },
    otherCard:{
        display:'inline',
    },
    button:{
        margin: 10,
        textAlign: 'center',
    },
    inlineImg:{
        display: 'inline-block',
        marginRight: 20,
    },
});

class AboutUs extends React.Component {

    render() {
        const {classes} = this.props;

        return (
            <div>
                <div className={classes.quote}>
                    "We will spend weeks, months and the World Soccer Tournament programming something we strongly believe will help our citizens do something about the forest."
                </div>

                <div className={classes.title}>
                    A equipa
                </div>

                <div className="imgcontainer">
                    <img src={require('./img/softwareelementalists.png')} alt="Avatar" width={'400'} />
                </div>

                <div id="cards" className="imgcontainer">
                    <Card className={classes.card}>
                        <CardMedia
                            className={classes.media}
                            image={require("./img/IMG_0464.JPG")}
                        />
                        <CardContent>
                            <div className={classes.name}>
                                Ana Margarida
                            </div>
                            <div className={classes.role}>
                                Web
                            </div>
                            <Typography component="p">
                                Especialista em React - JavaScript, CSS e HTML
                            </Typography>
                        </CardContent>
                        <CardActions>
                            <Button variant="raised" className={classes.button} style={{width: '100%'}}>
                                <MailIcon/> Contact
                            </Button>
                        </CardActions>
                    </Card>

                    <Card className={classes.card}>
                        <CardMedia
                            className={classes.media}
                            image={require("./img/daniel.jpg")}
                        />
                        <CardContent>
                            <div className={classes.name}>
                                Daniel Machado
                            </div>
                            <div className={classes.role}>
                                Server
                            </div>
                            <Typography component="p">
                                Especialista em Programar Server em Java
                            </Typography>
                        </CardContent>
                        <CardActions>
                            <Button variant="raised" className={classes.button} style={{width: '100%'}}>
                                <MailIcon/> Contact
                            </Button>
                        </CardActions>
                    </Card>

                    <Card className={classes.card}>
                        <CardMedia
                            className={classes.media}
                            image={require("./img/user.png")}
                        />
                        <CardContent>
                            <div className={classes.name}>
                                Francisco Antonio
                            </div>
                            <div className={classes.role}>
                                Androide
                            </div>
                            <Typography component="p">
                                Especialista em androide, municipios e verde-agua
                            </Typography>
                        </CardContent>
                        <CardActions>
                            <Button variant="raised" className={classes.button} style={{width: '100%'}}>
                                <MailIcon/> Contact
                            </Button>
                        </CardActions>
                    </Card>

                    <Card className={classes.card}>
                        <CardMedia
                            className={classes.media}
                            image={require("./img/marco.jpg")}
                        />
                        <CardContent>
                            <div className={classes.name}>
                                Marco Francisco
                            </div>
                            <div className={classes.role}>
                                Androide
                            </div>
                            <Typography component="p">
                                Especialista em androide
                            </Typography>
                        </CardContent>
                        <CardActions>
                            <Button variant="raised" className={classes.button} style={{width: '100%'}}>
                                <MailIcon/> Contact
                            </Button>
                        </CardActions>
                    </Card>

                    <Card className={classes.card}>
                        <CardMedia
                            className={classes.media}
                            image={require("./img/rui.jpg")}
                        />
                        <CardContent>
                            <div className={classes.name}>
                                Rui Genovevo
                            </div>
                            <div className={classes.role}>
                                Server
                            </div>
                            <Typography component="p">
                                Se vires o print que mandei para o grupo, as melhoras rui!
                            </Typography>
                        </CardContent>
                        <CardActions>
                            <Button variant="raised" className={classes.button} style={{width: '100%'}}>
                                <MailIcon/> Contact
                            </Button>
                        </CardActions>
                    </Card>
                </div>

                <div className={classes.title}>
                    Apresenta
                </div>

                <div className="imgcontainer">
                    <img src={require('./img/agniWithBackground.png')} alt="Avatar" />
                </div>

                <div className={classes.title}>
                    Com o apoio de
                </div>

                <div className="imgcontainer">
                    <img src={require('./img/material-ui.svg')} alt="Avatar" width={'100'} className={classes.inlineImg} />
                    <img src={require('./img/jshtmlcss.png')} alt="Avatar" width={'100'} className={classes.inlineImg} />
                    <img src={require('./img/java.jpg')} alt="Avatar" width={'100'} className={classes.inlineImg} />
                    <img src={require('./img/Android-Logo.jpg')} alt="Avatar" width={'100'} className={classes.inlineImg} />
                </div>


            </div>

        )
    }
}

export default withStyles(styles)(AboutUs);