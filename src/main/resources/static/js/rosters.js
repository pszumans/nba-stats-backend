//import {Table} from 'react-bootstrap'

class Rosters extends React.Component {
    constructor() {
        super();
        this.state = {
            teams: []
        };
    }

    componentDidMount() {

    fetch("/api/rosters")
        .then(response => response.json())
        .then(data => {
            this.setState({teams: data})
        })

     }

    render() {

        if(this.state.teams.length === 0){
            return <p>Loading...</p>
        } else {
//            const teamList = this.state.teams.map(team => {
//                        return <li>{team.fullName}</li>;
//                    });
            var rows = [];
            this.state.teams.forEach( team => {
                rows.push(<thead><tr key={team.teamId}><th>{team.fullName}</th></tr></thead>)
                let prows = [];
                team.players.forEach( player => {
                    prows.push(<tr key={player.personId}><td>{player.firstName}</td><td>{player.lastName}</td></tr>)
                })
                rows.push(<tbody>{prows}</tbody>)
            }
            )
         }
         return (<ReactBootstrap.Table striped>{rows}</ReactBootstrap.Table>);

        }
    }

ReactDOM.render(
    <Rosters /> ,
    document.getElementById('root')
);
