package examples;

import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

// Like all contracts, implements `Contract`.
public class ArtContract implements Contract {
    // Used to reference the contract in transactions.
    public static final String ID = "examples.ArtContract";

    // grouping
    public interface Commands extends CommandData {
        class Issue implements Commands { }
        class Transfer implements Commands { }
        class Exit implements Commands { }
    }

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);

        // Rules based on: structure, content, signers required
        // If Issue class instance, apply rules for Issue transactions.
        if (command.getValue() instanceof Commands.Issue) {
            // Issue transaction rules...
            // Checking transaction conditions/rules.
            //if (tx.getInputStates().size ), etc

        } else if (command.getValue() instanceof Commands.Transfer) {
            // Checking the shape of the transaction. Conditions.
            // Setting condition of 1 input state, 1 output state.
            if (tx.getInputStates().size() != 1) throw new IllegalArgumentException("Art transfer should have one inputs.");
            if (tx.getOutputStates().size() != 1) throw new IllegalArgumentException("Art transfer should have one output.");
            // checking for state.
            if (tx.inputsOfType(ArtState.class).size() != 1) throw new IllegalArgumentException("Art transfer input should be an ArtState.");
            if (tx.outputsOfType(ArtState.class).size() != 1) throw new IllegalArgumentException("Art transfer output should be an ArtState.");

            // Grabbing the transaction's contents.
            final ArtState artStateInput = tx.inputsOfType(ArtState.class).get(0);
            final ArtState artStateOutput = tx.outputsOfType(ArtState.class).get(0);

            // Checking the transaction's contents. Conditions.
            // Input and output states must contain same values for properties (artist, title, appraiser)
            if (!(artStateInput.getArtist().equals(artStateOutput.getArtist())))
                throw new IllegalArgumentException("Art transfer input and output should have the same artist.");
            if (!(artStateInput.getTitle().equals(artStateOutput.getTitle())))
                throw new IllegalArgumentException("Art transfer input and output should have the same title.");
            if (!(artStateInput.getAppraiser().equals(artStateOutput.getAppraiser())))
                throw new IllegalArgumentException("Art transfer input and output should have the same appraiser.");
            // owners changing for output state
            if (artStateInput.getOwner().equals(artStateOutput.getOwner()))
                throw new IllegalArgumentException("Art transfer input and output should have different owners.");

            // Checking the transaction's required signers.
            final List<PublicKey> requiredSigners = command.getSigners();
            if (!(requiredSigners.contains(artStateInput.getOwner().getOwningKey())))
                throw new IllegalArgumentException("Art transfer should have input's owner as a required signer.");
            if (!(requiredSigners.contains(artStateOutput.getOwner().getOwningKey())))
                throw new IllegalArgumentException("Art transfer should have output's owner as a required signer.");

            // If instance of Exit class transaction, apply rules for Exit class.
            // Ie. 0 output states, 1 (or 1+?) input states.
        } else if (command.getValue() instanceof Commands.Exit) {
            // Exit transaction rules...

        } else throw new IllegalArgumentException("Unrecognised command.");
    }
}