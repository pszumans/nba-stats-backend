class Greeting extends React.Component {
    constructor() {
        super();
        this.state = {
            teams: []
        };
    }

    componentDidMount() {

        fetch('http://localhost:8080/api/teams')
            .then((resp) => resp.json())
            .then((data) => {
                var teams = data._embedded.teams;
                this.setState({
                    teams: teams
                });
            });

    }

    render() {

        const list = this.state.teams.map(team => {
            return <li>{team.fullName}</li>;
        });
        return (<ul>{list}</ul>);
        }
    }

ReactDOM.render(
    <Greeting /> ,
    document.getElementById('root')
);