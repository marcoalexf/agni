import React from 'react';
import PropTypes from 'prop-types';
import Typography from 'material-ui/Typography';
import { withStyles } from 'material-ui/styles';
import Card, { CardContent, CardMedia } from 'material-ui/Card';
import Paper from 'material-ui/Paper';
import Button from 'material-ui/Button';
import ProfileIcon from '@material-ui/icons/PermIdentity';

const styles =  theme => ({
    title:{
        margin:20,
    },
    card: {
        width: 500,
        textAlign: 'center',
    },
    media: {
        paddingTop: '100%',
    },
    centerCard: {
        display: 'flex',
        justifyContent: 'center',
    },
    paper:theme.mixins.gutters({
        width: 600,
        padding: 40,
    }),
});

class Profile extends React.Component {

    loadInformations = () =>{
        var obj;
        var token = window.localStorage.getItem('token');

        if(token != null){
            var uname = JSON.parse(token).username;

            var xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "GET", "http://localhost:8080/rest/viewprofile/" + uname, true);

            xmlHttp.onload = function() {
                var response = xmlHttp.response;
                console.log("XML response: " + response);
                obj = JSON.parse(response);

                var username = document.getElementById("showusername");
                username.innerHTML = uname;

                var email = document.getElementById("showemail");
                email.innerHTML = obj.user_email;

                var role = document.getElementById("showrole");
                role.innerHTML = obj.user_role;
            }

            xmlHttp.send(null);
        }

        else{
            document.location.href = '/login';
        }
    }

    render() {
        const { classes } = this.props;
        return (
            <div onLoad={this.loadInformations}>
                <Typography variant="display1" className={classes.title}>Perfil</Typography>

                <Paper className={classes.paper} style={{margin: '0 auto'}} >

                    <div><Button style={{margin: 'left'}}> <ProfileIcon /> Editar Perfil </Button></div>
                    <div className="imgcontainer">
                        <img src={require('./img/user.png')} alt="Avatar" style={{margin: '0 auto'}}/>
                    </div>

                    <Card className={classes.card} style={{margin: '0 auto'}}>
                        <CardContent>
                            <Typography id="showusername" gutterBottom variant="headline" component="h2">

                            </Typography>
                            <Typography component="p">
                                Informações do utilizador:
                            </Typography>
                            <Typography id="showrole" component="p"></Typography>
                            <Typography id="showemail" component="p"></Typography>
                        </CardContent>
                    </Card>
                </Paper>
            </div>

        );
    }
}

Profile.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Profile);