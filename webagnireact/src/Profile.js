import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import Card, { CardActions, CardContent, CardMedia } from 'material-ui/Card';
import Button from 'material-ui/Button';
import Typography from 'material-ui/Typography';

const styles = {
    card: {
        maxWidth: 345,
    },
    media: {
        height: 0,
        paddingTop: '56.25%', // 16:9
    },
};

class Profile extends React.Component {
    render() {
        const { classes } = this.props;
        return (
            <div className="center">
                <div>
                    <Card className={classes.card}>
                        <CardMedia
                            className={classes.media}
                            image={require('./img/user.png')}
                            />
                        <CardContent>
                            <Typography gutterBottom variant="headline" component="h2">
                                Nome do utilizador
                            </Typography>
                            <Typography component="p">
                                Informações do utilizador:
                                Tipo
                                Email
                            </Typography>
                        </CardContent>
                    </Card>
                </div>
            </div>

        );
    }
}

Profile.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(Profile);