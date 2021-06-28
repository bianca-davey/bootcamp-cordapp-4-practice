package bootcamp;

import net.corda.core.contracts.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
LedgerTransaction class = T's
CommandWithParties class, commands set signers required

Output TokenState: issuer/owner participants, amount>0
Constraints:
0 input, 1 output, 1 command
Output = TokenState, amount > 0, Issue Command
Required signer- issuer

 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    // For T to ref the contract.
    public static String ID = "bootcamp.TokenContract";

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        //CommandWithParties<Command> command = requireSingleCommand(tx.getCommands(), Commands.class);
        //if (command.getValue() instanceof Commands.Issue) {

        if(tx.getInputStates().size() != 0){
            throw new IllegalArgumentException("Requires no input states.");
        }
        if(tx.getOutputStates().size() != 1){
            throw new IllegalArgumentException("Requires one output state.");
        }
        if(tx.outputsOfType(TokenState.class).size() !=1){
            throw new IllegalArgumentException("Issue output should be a TokenState.");
        }
        if(tx.getCommands().size() != 1){
            throw new IllegalArgumentException("Requires one command.");
        }

        // Checking output is an instance of TokenState.
        ContractState output = tx.getOutput(0);
        if(!(output instanceof TokenState)){
            throw new IllegalArgumentException("Output must be a TokenState.");
        }
        // Checking command is an issue.
        Command command = tx.getCommand(0);
        if(!(command.getValue() instanceof Commands.Issue)){
            throw new IllegalArgumentException("Command must be an Issue command.");
        }

        // Checking amount is above zero.
        TokenState token = (TokenState) output;
        if(token.getAmount() <= 0){
            throw new IllegalArgumentException("Amount must be above zero.");
        }

        // Checking required signer.
        final List<PublicKey> requiredSigners = command.getSigners();
        Party issuer = token.getIssuer();
        PublicKey issuerKey = issuer.getOwningKey();
        if(!(requiredSigners.contains(issuerKey))){
            throw new IllegalArgumentException("Issuer is a required signer.");
        }

    }

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }

}
