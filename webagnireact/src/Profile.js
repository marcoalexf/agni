import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import Card, { CardContent, CardMedia } from 'material-ui/Card';
import Typography from 'material-ui/Typography';

const styles = {
    card: {
        maxWidth: 345,
    },
    media: {
        paddingTop: '100%',
    },
    centerCard: {
        display: 'flex',
        justifyContent: 'center',
    }
};

class Profile extends React.Component {
    render() {
        const { classes } = this.props;
        return (
            <div className={classes.centerCard}>
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